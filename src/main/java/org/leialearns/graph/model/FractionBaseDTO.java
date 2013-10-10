package org.leialearns.graph.model;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.logic.model.Fraction;
import org.springframework.data.neo4j.annotation.GraphId;

import java.io.Serializable;

import static org.leialearns.utilities.Static.gcd;

public abstract class FractionBaseDTO extends BaseBridgeFacet implements HasId, Serializable, Comparable<FractionBaseDTO>, FarObject<Fraction> {

    @GraphId
    private Long id;

    private long numerator;
    private long denominator;

    private transient long gcd = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public abstract long getIndex();

    public abstract boolean getInOracle();

    public long getNumerator() {
        return numerator;
    }

    public void setNumerator(Long numerator) {
        this.numerator = numerator;
        gcd = 0L;
    }

    public long getDenominator() {
        return denominator;
    }

    public void setDenominator(Long denominator) {
        this.denominator = denominator;
        gcd = 0L;
    }

    public int compareTo(FractionBaseDTO other) {
        return Long.signum((numerator * other.denominator) - (other.numerator * denominator));
    }

    public boolean equals(Object other) {
        return other instanceof FractionBaseDTO && compareTo((FractionBaseDTO) other) == 0;
    }

    public int hashCode() {
        long n = getNumerator();
        long d = getDenominator();
        long g = gcd;
        long product = n * d;
        if (g == 0 && product != 0) {
            g = gcd(n, d);
            gcd = g;
        }
        return (int) (product / g);
    }

    public String toString() {
        return "[Fraction:(" + id + ":" + getInOracle() + ")" + getIndex() + "->" + getNumerator() + "/" + getDenominator() + "]";
    }

    public Fraction declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
