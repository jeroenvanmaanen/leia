package org.leialearns.logic.model;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.utilities.Expression;
import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.TreeSet;

import static org.leialearns.utilities.Static.getLoggingClass;

public class HistogramTrace {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final String name;
    private final Setting<Boolean> isTransient;
    private final Throwable origin;
    private final HistogramTrace leftTrace;
    private final String operator;
    private final HistogramTrace rightTrace;
    private final Histogram snapshot;

    public HistogramTrace(Histogram histogram) {
        this(histogram, "=", null);
    }

    public HistogramTrace(final Histogram left, String operator, Histogram right) {
        name = left.toString();
        isTransient = new Setting<>("Is transient?", new Expression<Boolean>() {
            public Boolean get() {
                return left.getVersion() == null;
            }
        });
        origin = left.getOrigin();
        leftTrace = left.getTrace();
        this.operator = operator;
        rightTrace = right == null ? null : right.getTrace();
        snapshot = left.getSnapshot("=");
    }

    public void log(String label) {
        Collection<Symbol> symbols = new TreeSet<>();
        collectSymbols(symbols);
        String qualifier = (label == null || label.isEmpty() ? "" : label + ": ");
        boolean empty = symbols.isEmpty() && leftTrace == null && rightTrace == null;
        logger.debug("Histogram: " + qualifier + name + (empty ? " {}" : " {"));
        if (!empty) {
            logParts("  ");
            for (Symbol symbol : symbols) {
                StringBuilder builder = new StringBuilder("  ");
                builder.append(symbol);
                builder.append("-> ");
                addValues(builder, symbol);
                logger.debug(builder.toString());
            }
            logger.debug("}");
        }
        if (logger.isTraceEnabled()) {
            HistogramTrace originTrace = findNonTransient();
            originTrace = (originTrace == null ? this : originTrace);
            logger.trace("Origin: " + originTrace.name + " (transient: " + originTrace.isTransient.get() + ")", originTrace.origin);
        }
    }

    protected HistogramTrace findNonTransient() {
        HistogramTrace result = null;
        if (isTransient.get()) {
            if (leftTrace != null) {
                result = leftTrace.findNonTransient();
            }
            if (result == null && rightTrace != null) {
                result = rightTrace.findNonTransient();
            }
        }
        return result;
    }

    protected void collectSymbols(Collection<Symbol> symbols) {
        addSymbols(snapshot, symbols);
        if (leftTrace != null) {
            leftTrace.collectSymbols(symbols);
        }
        if (rightTrace != null) {
            rightTrace.collectSymbols(symbols);
        }
    }

    protected void logParts(String indent) {
        if (leftTrace != null) {
            leftTrace.logParts(indent);
        }
        if (rightTrace != null) {
            rightTrace.logParts(indent + "  ");
        }
        logger.debug(indent + operator + " " + name);
    }

    protected void addValues(StringBuilder builder, Symbol symbol) {
        if (leftTrace != null) {
            leftTrace.addValues(builder, symbol);
            builder.append(", ");
        }
        if (rightTrace != null) {
            builder.append('(');
            rightTrace.addValues(builder, symbol);
            builder.append("), ");
        }
        long value = snapshot.getValue(symbol);
        builder.append(Long.toString(value));
    }

    protected void addSymbols(Histogram histogram, Collection<Symbol> symbols) {
        for (Counter counter : histogram.getCounters()) {
            symbols.add(counter.getSymbol());
        }
    }

}
