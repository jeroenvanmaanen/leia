package org.leialearns.logic.model;

import org.leialearns.bridge.NearIterable;

public interface Counter extends Locus {
    long getValue();
    void increment();
    void increment(long amount);
    void refresh();

    interface Iterable extends NearIterable<Counter> {
        Counter declareNearType();
    }
}
