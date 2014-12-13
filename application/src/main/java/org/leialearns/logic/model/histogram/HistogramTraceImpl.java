package org.leialearns.logic.model.histogram;

import org.leialearns.api.model.histogram.Counter;
import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.histogram.HistogramTrace;
import org.leialearns.common.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.TreeSet;

import static org.leialearns.common.Static.getLoggingClass;

public class HistogramTraceImpl implements HistogramTrace {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final String name;
    private final Setting<Boolean> isTransient;
    private final Throwable origin;
    private final HistogramTrace leftTrace;
    private final String operator;
    private final HistogramTrace rightTrace;
    private final Histogram snapshot;

    public HistogramTraceImpl(Histogram histogram) {
        this(histogram, "=", null);
    }

    public HistogramTraceImpl(final Histogram left, String operator, Histogram right) {
        name = left.toString();
        isTransient = new Setting<>("Is transient?", () -> !left.isPersistent());
        origin = left.getOrigin();
        leftTrace = left.getTrace();
        this.operator = operator;
        rightTrace = right == null ? null : right.getTrace();
        snapshot = left.getSnapshot("=");
    }

    public String getName() {
        return name;
    }

    public boolean isTransient() {
        return isTransient.get();
    }

    public Throwable getOrigin() {
        return origin;
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
            logger.trace("Origin: " + originTrace.getName() + " (transient: " + originTrace.isTransient() + ")", originTrace.getOrigin());
        }
    }

    public HistogramTrace findNonTransient() {
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

    public void collectSymbols(Collection<Symbol> symbols) {
        addSymbols(snapshot, symbols);
        if (leftTrace != null) {
            leftTrace.collectSymbols(symbols);
        }
        if (rightTrace != null) {
            rightTrace.collectSymbols(symbols);
        }
    }

    public void logParts(String indent) {
        if (leftTrace != null) {
            leftTrace.logParts(indent);
        }
        if (rightTrace != null) {
            rightTrace.logParts(indent + "  ");
        }
        logger.debug(indent + operator + " " + name);
    }

    public void addValues(StringBuilder builder, Symbol symbol) {
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
