package org.leialearns.logic.model.histogram;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.model.common.NodeData;
import org.leialearns.utilities.TypedIterable;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Histogram extends Modifiable, NodeData<Counter> {
    TypedIterable<Counter> getCounters();
    TypedIterable<Symbol> getSymbols();
    long getValue(Symbol symbol);
    long getWeight();
    boolean isEmpty();
    boolean isPersistent();
    void markPersistent();
    void setLabel(String label);
    void setLocation(Supplier<String> locationSupplier);
    void setCounterCreator(Function<Symbol,Counter> counterCreator);
    Throwable getOrigin();
    HistogramTrace getTrace();
    Histogram getSnapshot(String operator);
}
