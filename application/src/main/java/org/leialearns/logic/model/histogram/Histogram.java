package org.leialearns.logic.model.histogram;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.model.common.HasTypeLabel;
import org.leialearns.logic.model.common.NodeData;
import org.leialearns.utilities.TypedIterable;

public interface Histogram extends NodeData<Histogram,Counter.Iterable>, Modifiable, HasTypeLabel {
    TypedIterable<Counter> getCounters();
    long getValue(Symbol symbol);
    long getWeight();
    boolean isEmpty();
    void setLabel(String label);
    Throwable getOrigin();
    HistogramTrace getTrace();
    Histogram getSnapshot(String operator);
}
