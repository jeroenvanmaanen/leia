package org.leialearns.graph.model;

import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class FractionEstimateDTO extends FractionBaseDTO {

    @Override
    public long getIndex() {
        return -1;
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
