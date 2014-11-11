package org.leialearns.logic.model.histogram;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.utilities.Setting;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.leialearns.utilities.Static.getLoggingClass;

public class HistogramObject implements Histogram {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Map<Symbol, Counter> histogram = new HashMap<>();
    private final Throwable origin;
    private final Setting<String> label = new Setting<>("Label", "?");
    private final Setting<Boolean> persistent = new Setting<>("Persistent", false);
    private Supplier<String> locationSupplier = null;
    private Function<Symbol,Counter> counterCreator;
    private HistogramTrace trace = null;

    public HistogramObject() {
        origin = (logger.isTraceEnabled() ? new Throwable("Histogram origin") : null);
    }

    @Override
    public HistogramTrace getTrace() {
        return trace;
    }

    @Override
    public Throwable getOrigin() {
        return origin;
    }

    @Override
    public boolean isPersistent() {
        return persistent.get();
    }

    @Override
    public void markPersistent() {
        persistent.set(true);
    }

    @Override
    public void setLabel(String label) {
        this.label.set(label);
    }

    public String getLabel() {
        return label.get();
    }

    @Override
    public void setLocation(Supplier<String> locationSupplier) {
        this.locationSupplier = locationSupplier;
    }

    @Override
    public void setCounterCreator(Function<Symbol,Counter> counterCreator) {
        this.counterCreator = counterCreator;
    }

    @Override
    public TypedIterable<Counter> getCounters() {
        return new TypedIterable<>(histogram.values(), Counter.class);
    }

    @Override
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
    public void retrieve(Supplier<Iterable<Counter>> countersSupplier) {
        histogram.clear();
        Iterable<Counter> counters = countersSupplier.get();
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
                logger.error("Histogram add failed: " + locationSupplier.get());
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
                            String message = "Missing counter: " + locationSupplier.get() + ": " + symbol + " -= " + counter;
                            logger.warn(message);
                            throw new IllegalStateException(message);
                        }
                        newCounter.increment(-value);
                    }
                }
                addTrace("-", other);
            } catch (RuntimeException exception) {
                logger.error("Histogram subtract failed: " + locationSupplier.get() + ": " + symbol);
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

    @Override
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
                newCounter = counterCreator.apply(symbol);
                histogram.put(symbol, newCounter);
            }
            newCounter.increment(counter.getValue());
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
