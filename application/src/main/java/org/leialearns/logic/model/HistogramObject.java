package org.leialearns.logic.model;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.leialearns.utilities.Static.getLoggingClass;

public class HistogramObject extends BaseDistribution implements Histogram {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Map<Symbol, Counter> histogram = new HashMap<>();
    private final Throwable origin;
    private HistogramTrace trace = null;

    public HistogramObject() {
        origin = (logger.isTraceEnabled() ? new Throwable("Histogram origin") : null);
    }

    public HistogramTrace getTrace() {
        return trace;
    }

    public Throwable getOrigin() {
        return origin;
    }

    @Override
    public TypedIterable<Counter> getCounters() {
        return new TypedIterable<>(histogram.values(), Counter.class);
    }

    public long getValue(Symbol symbol) {
        long result;
        if (histogram.containsKey(symbol)) {
            result = histogram.get(symbol).getValue();
        } else {
            result = 0L;
        }
        return result;
    }

    @Override
    public long getWeight() {
        long result = 0L;
        for (Counter counter : histogram.values()) {
            if (counter != null) {
                long value = counter.getValue();
                if (value > 0) {
                    result += value;
                }
            }
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        boolean result = true;
        for (Counter counter : histogram.values()) {
            if (counter.getValue() > 0) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public void retrieve() {
        histogram.clear();
        Counter.Iterable counters = getVersion().findCounters(getNode());
        for (Counter counter : counters) {
            histogram.put(counter.getSymbol(), counter.fresh());
        }
        if (logger.isTraceEnabled()) {
            trace = new HistogramTrace(this);
        }
    }

    @Override
    public void add(Histogram other) {
        if (!other.isEmpty()) {
            try {
                addInternal(other);
                addTrace("+", other);
            } catch (RuntimeException exception) {
                logger.error("Histogram add failed: " + getNode());
                if (logger.isTraceEnabled()) {
                    log();
                    other.log("other");
                }
                throw exception;
            }
        }
    }

    @Override
    public void subtract(Histogram other) {
        if (!other.isEmpty()) {
            Symbol symbol = null;
            try {
                for (Counter counter : other.getCounters()) {
                    symbol = counter.getSymbol();
                    long value = counter.getValue();
                    if (value > 0) {
                        Counter newCounter = histogram.get(symbol);
                        if (newCounter == null) {
                            String message = "Missing counter: " + getNode() + ": " + symbol + " -= " + counter;
                            logger.warn(message);
                            throw new IllegalStateException(message);
                        }
                        newCounter.increment(-value);
                    }
                }
                addTrace("-", other);
            } catch (RuntimeException exception) {
                logger.error("Histogram subtract failed: " + getNode() + ": " + symbol);
                if (logger.isTraceEnabled()) {
                    log();
                    other.log("other");
                }
                throw exception;
            }
        }
    }

    protected void addTrace(String operator, Histogram operand) {
        if (logger.isTraceEnabled()) {
            trace = new HistogramTrace(this, operator, operand);
        }
    }

    public HistogramObject getSnapshot(String operator) {
        HistogramObject result = new HistogramObject();
        result.setLabel("(" + operator + ")" + getLabel());
        result.addInternal(this);
        return result;
    }

    protected void addInternal(Histogram other) {
        for (Counter counter : other.getCounters()) {
            Symbol symbol = counter.getSymbol();
            Counter newCounter;
            if (histogram.containsKey(symbol)) {
                newCounter = histogram.get(symbol);
            } else {
                if (getPersistent()) {
                    newCounter = getVersion().findOrCreateCounter(getNode(), symbol);
                } else {
                    newCounter = new TransientCounter(symbol);
                }
                histogram.put(symbol, newCounter);
            }
            newCounter.increment(counter.getValue());
            // counterDAO.save(counter);
        }
    }

    @Override
    public void log() {
        log(null);
    }

    @Override
    public void log(String label) {
        HistogramTrace trace = this.trace;
        if (trace == null) {
            trace = new HistogramTrace(this);
        }
        trace.log(label);
    }

    @Override
    public String getTypeLabel() {
        return "Histogram";
    }

}
