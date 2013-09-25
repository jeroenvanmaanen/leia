package org.leialearns.logic.model;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.session.Session;
import org.leialearns.logic.structure.Node;
import org.leialearns.utilities.Function;
import org.leialearns.utilities.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Display.show;
import static org.leialearns.utilities.Static.getLoggingClass;

public class CounterLogger {
    private static final double LOG10BASE2 = Math.log(10) / Math.log(2);
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    public void logCounters(Counted counted) {
        logCounters(counted.getVersion());
    }

    public void logCounters(Observed observed) {
        logCounters(observed, null);
    }

    public void logCounters(Expected expected) {
        logCounters(expected, null);
    }

    public void logCounters(Observed observed, Node node) {
        logger.info("Logging counters for: " + observed);
        logCounters(null, node, new ExpectedNote(observed, observed.getVersion().getOwner()), observed.getVersion(), observed.getDeltaVersion());
    }

    public void logCounters(Expected expected, Node node) {
        logger.info("Logging counters for: " + expected);
        Observed observed = expected.getObserved();
        if (observed != null) {
            logCounters(null, node, new ExpectedNote(expected, expected.getVersion().getOwner()), observed.getVersion(), observed.getDeltaVersion());
        }
    }

    public void logCounters(Version... version) {
        logCounters((String) null, version);
    }

    public void logCounters(String label, Version... version) {
        logCounters(label, null, null, version);
    }

    public void logCounters(Node node, Version... version) {
        logCounters(null, node, null, version);
    }

    public void logCounters(String label, Node node, Function<Pair<Node,Symbol>,String> note, Version... versions) {
        if (logger.isInfoEnabled()) {
            String prefix = (label != null && label.length() > 0 ? label + ": " : "");
            int[] widths = new int[3];
            String suffix = "Counters for versions: " + display(versions);
            Iterable<Counter> counters;
            if (node != null) {
                suffix += ": node: [" + display(node) + "]";
            }
            SortedMap<String,SortedMap<String,Counter[]>> data = new TreeMap<String, SortedMap<String, Counter[]>>();
            int index = 0;
            for (Version version : versions) {
                if (version == null) {
                    continue;
                }
                if (node == null) {
                    counters = version.findCounters();
                } else {
                    counters = version.findCounters(node);
                }
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
                pathValues = new TreeMap<String, Counter[]>();
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
                    messageBuilder.append(note.get(new Pair<Node, Symbol>(node, symbolObject)));
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

    protected class ExpectedNote implements Function<Pair<Node,Symbol>,String> {
        private final ExpectedModel expectedModel;
        private final Session session;
        protected ExpectedNote(Observed observed, Session session) {
            this(observed.getExpectedModel(), session);
        }
        protected ExpectedNote(ExpectedModel expectedModel, Session session) {
            this.expectedModel = expectedModel;
            this.session = session;
        }
        public String get(Pair<Node,Symbol> pair) {
            Node node = pair.getLeft();
            Symbol symbol = pair.getRight();
            boolean isIncluded = expectedModel.isIncluded(node, session);
            String result = " (i:" + expectedModel.isIncluded(node, session) + ")";
            if (isIncluded) {
                Expectation expectation = expectedModel.getExpectation(node);
                if (expectation == null) {
                    result += " ??";
                } else {
                    Fraction fraction = expectation.getFraction(symbol);
                    if (fraction == null) {
                        result += " ?";
                    } else {
                        result = result + " " + fraction;
                        if (fraction.getNumerator() == 0L) {
                            result = result + " " + expectation;
                        }
                    }
                }
            }
            return result;
        }
    }

}
