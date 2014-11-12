package org.leialearns.api.model.histogram;

import org.leialearns.bridge.NearIterable;
import org.leialearns.api.model.common.Locus;

public interface Counter extends Locus {
    long getValue();
    void increment();
    void increment(long amount);
    Counter fresh();

    interface Iterable extends NearIterable<Counter> {
        Counter declareNearType();
    }
}
