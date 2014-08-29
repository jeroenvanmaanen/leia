package org.leialearns.graph.model;

import com.google.common.base.Objects;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
@TypeAlias("FractionOracle")
public class FractionOracleDTO extends FractionBaseDTO {

    @GraphId
    private Long id;

    private long numerator;
    private long denominator;

    private transient long gcd = 0;

    @Indexed(unique = true, numeric = false)
    private Long index;

    @Override
    public Long getId() {
        return id;
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
    public void setId(Long id) {
        this.id = id;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    @Override
    public Long getIndex() {
        return Objects.firstNonNull(index, -1L);
    }

    @Override
    public boolean getInOracle() {
        return true;
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
