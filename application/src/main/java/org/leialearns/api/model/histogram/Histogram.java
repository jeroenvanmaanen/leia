package org.leialearns.api.model.histogram;

import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.common.NodeData;
import org.leialearns.utilities.TypedIterable;

import java.util.function.Function;

public interface Histogram extends Modifiable, NodeData<Counter> {
    TypedIterable<Counter> getCounters();
    TypedIterable<Symbol> getSymbols();
    long getValue(Symbol symbol);
    long getWeight();
    boolean isEmpty();
    boolean isPersistent();
    void markPersistent();
    void setLabel(String label);
    void setCounterCreator(Function<Symbol,Counter> counterCreator);
    Throwable getOrigin();
    HistogramTrace getTrace();
    Histogram getSnapshot(String operator);
}
