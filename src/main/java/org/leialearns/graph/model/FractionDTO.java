package org.leialearns.graph.model;

import org.leialearns.bridge.FarObject;
import org.leialearns.graph.BaseGraphDTO;
import org.leialearns.logic.model.Fraction;
import java.io.Serializable;

public class FractionDTO extends BaseGraphDTO implements Serializable, Comparable<FractionDTO>, FarObject<Fraction> {

    public Long getId() {
        return null; // TODO: implement
    }

    public void setId(Long id) {
        // TODO: implement
    }

    public void setIndex(long index) {
        // TODO: implement
    }

    public long getIndex() {
        return 0; // TODO: implement
    }

    public void setInOracle(boolean inOracle) {
        // TODO: implement
    }

    public boolean getInOracle() {
        return false; // TODO: implement
    }

    public long getNumerator() {
        return 0; // TODO: implement
    }

    public void setNumerator(Long numerator) {
        // TODO: implement
    }

    public long getDenominator() {
        return 0; // TODO: implement
    }

    public void setDenominator(Long denominator) {
        // TODO: implement
    }

    public int compareTo(FractionDTO other) {
        return 0; // TODO: implement
    }

    public boolean equals(Object other) {
        return false; // TODO: implement
    }

    public int hashCode() {
        return 0; // TODO: implement
    }

    public String toString() {
        return null; // TODO: implement
    }

    public Fraction declareNearType() {
        return null; // TODO: implement
    }

}
