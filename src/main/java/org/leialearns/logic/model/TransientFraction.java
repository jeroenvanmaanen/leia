package org.leialearns.logic.model;

import java.math.BigInteger;

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

    public int compareTo(Fraction other) {
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
