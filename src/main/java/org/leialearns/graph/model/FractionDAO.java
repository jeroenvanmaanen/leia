package org.leialearns.graph.model;

import org.leialearns.graph.session.RootDTO;
import org.leialearns.logic.model.Fraction;
import org.leialearns.utilities.TypedIterable;

public class FractionDAO {

    public TypedIterable<FractionDTO> findFractions(RootDTO root) {
        return null; // TODO: implement
    }

    public FractionDTO findFraction(RootDTO root, long index) {
        return null; // TODO: implement
    }

    public FractionDTO findOrCreateFraction(RootDTO root, Fraction fraction) {
        return null; // TODO: implement
    }

    public FractionDTO findOrCreateFraction(RootDTO root, long index, long numerator, long denominator) {
        return null; // TODO: implement
    }

    public FractionDTO createFraction(RootDTO root, long index, long numerator, long denominator) {
        return createFraction(root, index, numerator, denominator, false);
    }

    public FractionDTO createFraction(RootDTO root, long index, long numerator, long denominator, boolean inOracle) {
        return null; // TODO: implement
    }

}
