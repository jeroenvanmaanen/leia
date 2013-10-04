package org.leialearns.graph.model;

import org.leialearns.graph.session.RootDTO;
import org.leialearns.logic.model.Fraction;
import org.leialearns.utilities.TypedIterable;

public class FractionDAO {

    public TypedIterable<FractionDTO> findFractions(RootDTO root) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public FractionDTO findFraction(RootDTO root, long index) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public FractionDTO findOrCreateFraction(RootDTO root, Fraction fraction) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public FractionDTO findOrCreateFraction(RootDTO root, long index, long numerator, long denominator) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public FractionDTO createFraction(RootDTO root, long index, long numerator, long denominator) {
        return createFraction(root, index, numerator, denominator, false);
    }

    public FractionDTO createFraction(RootDTO root, long index, long numerator, long denominator, boolean inOracle) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

}
