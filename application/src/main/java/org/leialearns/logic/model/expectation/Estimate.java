package org.leialearns.logic.model.expectation;

import org.leialearns.bridge.NearIterable;
import org.leialearns.logic.model.common.Locus;

public interface Estimate extends Locus {
    Fraction getFraction();

    public interface Iterable extends NearIterable<Estimate> {
        @Override
        Estimate declareNearType();
    }
}
