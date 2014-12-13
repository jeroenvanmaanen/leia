package org.leialearns.logic.model;

import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.Counted;
import org.leialearns.api.model.CounterLogger;
import org.leialearns.api.model.Expected;
import org.leialearns.api.model.ExpectedModel;
import org.leialearns.api.model.Observed;
import org.leialearns.api.model.Version;
import org.leialearns.api.model.expectation.Expectation;
import org.leialearns.api.model.expectation.Fraction;
import org.leialearns.api.model.histogram.Counter;
import org.leialearns.api.structure.Node;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.utilities.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import static org.leialearns.common.Display.display;
import static org.leialearns.common.Display.show;
import static org.leialearns.common.Static.getLoggingClass;

public class CounterLoggerImpl implements CounterLogger {
    private static final double LOG10BASE2 = Math.log(10) / Math.log(2);
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Override
    public void logCounters(Counted counted) {
        logCounters(counted.getVersion());
    }

    @BridgeOverride
    public void logCounters(Observed observed) {
        logCounters(observed, null);
    }

    @BridgeOverride
    public void logCounters(Expected expected) {
        logCounters(expected, null);
    }

    public void logCounters(Observed observed, Node node) {
        logger.info("Logging counters for: " + observed);
        ExpectedNoteCache expectedNoteCache = new ExpectedNoteCache(observed);
        logCounters(null, node, new ExpectedNote(expectedNoteCache), observed.getVersion(), observed.getDeltaVersion());
    }

    public void logCounters(Expected expected, Node node) {
        logger.info("Logging counters for: " + expected);
        Observed observed = expected.getObserved();
        if (observed != null) {
            ExpectedNoteCache expectedNoteCache = new ExpectedNoteCache(expected);
            logCounters(null, node, new ExpectedNote(expectedNoteCache), observed.getVersion(), observed.getDeltaVersion());
        }
    }

    @BridgeOverride
    public void logModel(final Expected expected) {
        logger.info("Logging model: " + expected);
        Observed observed = expected.getObserved();
        if (observed != null) {
            Function<Version,Iterable<Counter>> getCounters = version -> version.findCounters(null, expected::isIncluded);
            ExpectedNoteCache expectedNoteCache = new ExpectedNoteCache(expected);
            logCounters(null, getCounters, new ExpectedNote(expectedNoteCache), observed.getVersion(), observed.getDeltaVersion());
        }
    }

    public void logCounters(Version... version) {
        logCounters((String) null, version);
    }

    public void logCounters(String label, Version... version) {
        logCounters(label, (Node) null, null, version);
    }

    @Override
    public void logCounters(Node node, Version... version) {
        logCounters(null, node, null, version);
    }

    public void logCounters(String label, final Node node, Function<Pair<Node,Symbol>,String> note, Version... versions) {
        if (logger.isInfoEnabled()) {
            String prefix = (label != null && label.length() > 0 ? label + ": " : "");
            if (node != null) {
                prefix += "node: [" + display(node) + "]: ";
            }
            Function<Version,Iterable<Counter>> getCounters;
            if (node == null) {
                getCounters = Version::findCounters;
            } else {
                getCounters = version -> version.findCounters(node);
            }
            logCounters(prefix, getCounters, note, versions);
        }
    }

    public void logCounters(String label, Function<Version,Iterable<Counter>> getCounters, Function<Pair<Node,Symbol>,String> note, Version... versions) {
        if (logger.isInfoEnabled()) {
            String prefix = (label != null && label.length() > 0 ? label + ": " : "");
            int[] widths = new int[3];
            String suffix = "Counters for versions: " + display(versions);
            Iterable<Counter> counters;
            SortedMap<String,SortedMap<String,Counter[]>> data = new TreeMap<>();
            int index = 0;
            for (Version version : versions) {
                if (version == null) {
                    continue;
                }
                counters = getCounters.apply(version);
                arrangeCounters(data, counters, versions.length, index, widths);
                index++;
            }
            logCounters(prefix + suffix, data, widths[0], widths[1], widths[2], note);
        }
    }

    protected void arrangeCounters(SortedMap<String,SortedMap<String,Counter[]>> data, Iterable<Counter> counters, int nrVersions, int index, int[] widths) {
        int pathWidth = 1;
        int symbolWidth = 1;
        int valueWidth = 1;
        for (Counter counter : counters) {
            Node node = counter.getNode();

            StringBuilder builder = new StringBuilder();
            node.showPath(builder);
            String path = builder.toString();
            pathWidth = Math.max(pathWidth, path.length());

            SortedMap<String,Counter[]> pathValues;
            if (data.containsKey(path)) {
                pathValues = data.get(path);
            } else {
                pathValues = new TreeMap<>();
                data.put(path, pathValues);
            }
            String symbol = show(counter.getSymbol().getDenotation());
            symbolWidth = Math.max(symbolWidth, symbol.length());
            Counter[] symbolCounters;
            if (pathValues.containsKey(symbol)) {
                symbolCounters = pathValues.get(symbol);
            } else {
                symbolCounters = new Counter[nrVersions];
                pathValues.put(symbol, symbolCounters);
            }
            valueWidth = Math.max(valueWidth, getValueWidth(counter));
            symbolCounters[index] = counter;
        }
        if (widths != null) {
            int[] newWidths = new int[] { pathWidth, symbolWidth, valueWidth };
            for (int i = 0; i < widths.length && i < newWidths.length; i++) {
                widths[i] = Math.max(widths[i], newWidths[i]);
            }
        }
    }

    protected int getValueWidth(Counter counter) {
        BigInteger value = BigInteger.valueOf(counter.getValue());
        int bitLength = value.bitLength();
        int result = (int) Math.ceil(bitLength / LOG10BASE2);
        if (logger.isTraceEnabled()) {
            logger.trace("Value width: " + counter.getValue() + ": " + bitLength + ": " + result);
        }
        return result;
    }

    protected void logCounters(String label, SortedMap<String,SortedMap<String,Counter[]>> nodes, int pathWidth, int symbolWidth, int valueWidth, Function<Pair<Node,Symbol>,String> note) {
        logger.trace("Constant: log_2(10) = " + LOG10BASE2);

        String pathPadding = createPadding(pathWidth, false);
        String symbolPadding = createPadding(symbolWidth, true, "  .");
        String valuePadding = createPadding(valueWidth, false);

        logger.info(label + ": {");
        for (Map.Entry<String,SortedMap<String,Counter[]>> valuesEntry : nodes.entrySet()) {
            for (Map.Entry<String,Counter[]> entry : valuesEntry.getValue().entrySet()) {
                Counter[] symbolCounters = entry.getValue();
                Node node = null;
                for (Counter counter : symbolCounters) {
                    if (counter == null) {
                        continue;
                    }
                    node = counter.getNode();
                }
                if (node == null) {
                    continue;
                }

                StringBuilder builder = new StringBuilder();
                node.showPathReverse(builder);
                builder.insert(0, pathPadding.substring(0, pathPadding.length() - builder.length()));
                String path = builder.toString();

                String symbol = entry.getKey();
                String padding = symbol.length() < symbolPadding.length() ? ' ' + symbolPadding.substring(symbol.length() + 1) : "";
                symbol = "[" + symbol + "]" + padding;

                StringBuilder messageBuilder = new StringBuilder("  " + path + " " + symbol + " = ");
                boolean first = true;
                Symbol symbolObject = null;
                for (Counter counter : symbolCounters) {
                    if (first) {
                        first = false;
                    } else {
                        messageBuilder.append(", ");
                    }
                    String value;
                    if (counter == null) {
                        value = "-";
                    } else {
                        value = Long.toString(counter.getValue());
                        if (symbolObject == null) {
                            symbolObject = counter.getSymbol();
                        }
                    }
                    if (value.length() < valueWidth) {
                        value = valuePadding.substring(0, valuePadding.length() - value.length()) + value;
                    }
                    messageBuilder.append(value);
                }
                if (note != null) {
                    messageBuilder.append(note.apply(new Pair<>(node, symbolObject)));
                }
                logger.info(messageBuilder.toString());
            }
            logger.info("");
        }
        logger.info("}");
    }

    @SuppressWarnings("unused")
    protected String createPadding(int width) {
        return createPadding(width, true);
    }

    protected String createPadding(int width, boolean padRight) {
        return createPadding(width, padRight, "                  ");
    }

    protected String createPadding(int width, boolean padRight, String chunk) {
        StringBuilder builder;
        builder = new StringBuilder();
        while (builder.length() < width) {
            builder.append(chunk);
        }
        if (builder.length() > width) {
            if (padRight) {
                builder.replace(0, builder.length() - width, "");
            } else {
                builder.setLength(width);
            }
        }
        return builder.toString();
    }

    protected class ExpectedNoteCache {
        private final ExpectedModel expectedModel;
        private Map<Node,Boolean> includeFlagCache = new HashMap<>();
        private Map<Node,Expectation> expectationCache = new HashMap<>();
        protected ExpectedNoteCache(Observed observed) {
            this(observed.getExpectedModel());
        }
        protected ExpectedNoteCache(ExpectedModel expectedModel) {
            this.expectedModel = expectedModel;
        }
        public boolean isIncluded(Node node) {
            boolean result;
            if (includeFlagCache.containsKey(node)) {
                result = includeFlagCache.get(node);
            } else {
                result = expectedModel.isIncluded(node);
                includeFlagCache.put(node, result);
            }
            return result;
        }
        public Expectation getExpectation(Node node) {
            Expectation result;
            if (expectationCache.containsKey(node)) {
                result = expectationCache.get(node);
            } else {
                result = expectedModel.getExpectation(node);
                expectationCache.put(node, result);
            }
            return result;
        }
    }

    protected class ExpectedNote implements Function<Pair<Node,Symbol>,String> {
        private final ExpectedNoteCache cache;
        protected ExpectedNote(ExpectedNoteCache cache) {
            this.cache = cache;
        }
        @Override
        public String apply(Pair<Node,Symbol> pair) {
            Node node = pair.getLeft();
            Symbol symbol = pair.getRight();
            boolean isIncluded = cache.isIncluded(node);
            String result = " (i:" + isIncluded + ")";
            if (isIncluded) {
                Expectation expectation = cache.getExpectation(node);
                if (expectation == null) {
                    result += " ??";
                } else {
                    Fraction fraction = expectation.getFraction(symbol);
                    if (fraction == null) {
                        result += " ?";
                    } else {
                        result = result + " " + fraction.getNumerator() + "/" + fraction.getDenominator();
                        if (fraction.getNumerator() == 0L) {
                            result = result + " " + fraction + " " + expectation;
                        }
                    }
                }
            }
            return result;
        }
    }
}
