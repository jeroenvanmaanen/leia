package org.leialearns.logic.model.histogram;

import org.leialearns.bridge.NearIterable;
import org.leialearns.logic.model.Locus;

public interface Counter extends Locus {
    long getValue();
    void increment();
    void increment(long amount);
    Counter fresh();

    interface Iterable extends NearIterable<Counter> {
        Counter declareNearType();
    }
}
