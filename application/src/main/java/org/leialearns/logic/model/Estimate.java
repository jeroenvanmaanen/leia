package org.leialearns.logic.model;

import org.leialearns.bridge.NearIterable;

public interface Estimate extends Locus {
    Fraction getFraction();

    public interface Iterable extends NearIterable<Estimate> {
        @Override
        Estimate declareNearType();
    }
}
