package org.leialearns.logic.model;

import org.leialearns.logic.session.Root;

public class FractionHelper {

    public Fraction createTransientFraction(Root root, long index, long numerator, long denominator) {
        return new TransientFraction(index, numerator, denominator);
    }

    public int compareTo(Fraction thisFraction, Object that) {
        int result;
        if (that instanceof Fraction) {
            Fraction thatFraction = (Fraction) that;
            result = Long.signum((thisFraction.getNumerator() * thatFraction.getDenominator()) - (thatFraction.getNumerator() * thisFraction.getDenominator()));
        } else {
            throw new IllegalArgumentException("Not a fraction: " + that);
        }
        return result;
    }

}
