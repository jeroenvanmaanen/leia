package org.leialearns.api.model.histogram;

import org.leialearns.api.common.Locus;
import org.leialearns.bridge.NearIterable;

public interface Counter extends Locus {
    long getValue();
    void increment();
    void increment(long amount);
    Counter fresh();

    interface Iterable extends NearIterable<Counter> {
        Counter declareNearType();
    }
}
