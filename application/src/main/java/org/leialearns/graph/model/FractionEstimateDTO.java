package org.leialearns.graph.model;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class FractionEstimateDTO extends FractionBaseDTO {

    @GraphId
    private Long id;

    private long numerator;
    private long denominator;

    private transient long gcd = 0;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public long getNumerator() {
        return numerator;
    }

    public void setNumerator(Long numerator) {
        this.numerator = numerator;
        setGCD(0L);
    }

    @Override
    public long getDenominator() {
        return denominator;
    }

    public void setDenominator(Long denominator) {
        this.denominator = denominator;
        setGCD(0L);
    }

    @Override
    public long getGCD() {
        return gcd;
    }

    @Override
    public void setGCD(long gcd) {
        this.gcd = gcd;
    }

    @Override
    public Long getIndex() {
        return -1L;
    }

    @Override
    public boolean getInOracle() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
