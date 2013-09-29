package org.leialearns.graph.structure;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.logic.structure.Structure;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import java.io.Serializable;

import static org.leialearns.utilities.Display.displayParts;

@NodeEntity
public class StructureDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Structure> {
    @GraphId
    private Long id;

    @Indexed(unique = true)
    private String uri;

    private int maxDepth;

    @Override
    public Long getId() {
        return null; // TODO: implement
    }

    @Override
    public void setId(Long id) {
        // TODO: implement
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public String toString() {
        return displayParts("Structure", uri, maxDepth);
    }

    public void markExtensible(NodeDTO node) {
        // TODO: implement
    }

    @Override
    public boolean equals(Object other) {
        return false; // TODO: implement
    }

    @Override
    public Structure declareNearType() {
        return null; // TODO: implement
    }

}
