package org.leialearns.api.model.expectation;

import org.leialearns.api.common.Locus;
import org.leialearns.bridge.NearIterable;

public interface Estimate extends Locus {
    Fraction getFraction();

    public interface Iterable extends NearIterable<Estimate> {
        @Override
        Estimate declareNearType();
    }
}
