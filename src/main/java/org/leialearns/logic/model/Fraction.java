package org.leialearns.logic.model;

import org.leialearns.bridge.NearIterable;

public interface Fraction extends Comparable<Fraction> {
    long getIndex();
    long getNumerator();
    long getDenominator();
    Fraction add(Fraction other);

    interface Iterable extends NearIterable<Fraction> {
        Fraction declareNearType();
    }
}
