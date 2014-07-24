package org.leialearns.logic.model;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.utilities.TypedIterable;

public interface Histogram extends Distribution, Modifiable {
    TypedIterable<Counter> getCounters();
    long getValue(Symbol symbol);
    long getWeight();
    boolean isEmpty();
    void retrieve();
    void setLabel(String label);
    Throwable getOrigin();
    HistogramTrace getTrace();
    Histogram getSnapshot(String operator);
}
