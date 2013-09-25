package org.leialearns.logic.model;

import org.leialearns.bridge.NearIterable;

public interface Fraction extends Comparable<Fraction> {
    long getIndex();
    long getNumerator();
    long getDenominator();

    interface Iterable extends NearIterable<Fraction> {
        Fraction declareNearType();
    }
}
