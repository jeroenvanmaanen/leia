package org.leialearns.graph.model;

import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class FractionOracleDTO extends FractionBaseDTO {

    @Indexed(unique = true, indexName = "oracleFractionIndex")
    private long index;

    public void setIndex(long index) {
        this.index = index;
    }

    @Override
    public long getIndex() {
        return index;
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
