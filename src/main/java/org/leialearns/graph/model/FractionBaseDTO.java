package org.leialearns.graph.model;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.logic.model.Fraction;

import java.io.Serializable;

import static org.leialearns.utilities.Static.gcd;

public abstract class FractionBaseDTO extends BaseBridgeFacet implements HasId, Serializable, Comparable<FractionBaseDTO>, FarObject<Fraction> {

    abstract public Long getId();

    abstract public void setId(Long id);

    public abstract long getNumerator();
    public abstract long getDenominator();
    public abstract long getGCD();
    public abstract void setGCD(long gcd);
    public abstract Long getIndex();
    public abstract boolean getInOracle();

    public int compareTo(FractionBaseDTO other) {
        return Long.signum((getNumerator() * other.getDenominator()) - (other.getNumerator() * getDenominator()));
    }

    public boolean equals(Object other) {
        return other instanceof FractionBaseDTO && compareTo((FractionBaseDTO) other) == 0;
    }

    public int hashCode() {
        long n = getNumerator();
        long d = getDenominator();
        long g = getGCD();
        long product = n * d;
        if (g == 0 && product != 0) {
            g = gcd(n, d);
            setGCD(g);
        }
        return (int) (product / g);
    }

    public String toString() {
        return "[Fraction:(" + getId() + ":" + getInOracle() + ")" + getIndex() + "->" + getNumerator() + "/" + getDenominator() + "]";
    }

    public Fraction declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
