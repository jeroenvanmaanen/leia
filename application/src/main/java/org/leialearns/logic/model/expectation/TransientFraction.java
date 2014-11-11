package org.leialearns.logic.model.expectation;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

import static org.leialearns.utilities.Static.gcd;

public class TransientFraction implements Fraction {
    private final long index;
    private final long numerator;
    private final long denominator;

    public TransientFraction(long index, long numerator, long  denominator) {
        long gcd = BigInteger.valueOf(numerator).gcd(BigInteger.valueOf(denominator)).longValue();
        if (gcd < 1L) {
            gcd = 1L;
        }
        this.index = index;
        this.numerator = numerator / gcd;
        this.denominator = denominator / gcd;
    }

    public long getIndex() {
        return index;
    }

    public long getNumerator() {
        return numerator;
    }

    public long getDenominator() {
        return denominator;
    }

    public Fraction add(Fraction other) {
        long a = getNumerator();
        long b = getDenominator();
        long c = other.getNumerator();
        long d = other.getDenominator();
        long gcd = gcd(b, d);
        d = d / gcd;
        long denominator = b * d;
        b = b / gcd;
        long numerator = (a * d) + (c * b);
        return new TransientFraction(-1, numerator, denominator);
    }

    public int compareTo(@NotNull Fraction other) {
        return Long.signum((numerator * other.getDenominator()) - (other.getNumerator() * denominator));
    }

    public boolean equals(Object other) {
        return other instanceof Fraction && compareTo((Fraction) other) == 0;
    }

    public int hashCode() {
        return (int) (numerator * denominator);
    }

    public String toString() {
        return "[TransientFraction:" + index + "->" + numerator + "/" + denominator + "]";
    }

}
