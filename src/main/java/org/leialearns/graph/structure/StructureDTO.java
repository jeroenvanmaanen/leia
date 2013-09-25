package org.leialearns.graph.structure;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.logic.structure.Structure;
import java.io.Serializable;

public class StructureDTO extends BaseBridgeFacet implements Serializable, HasId, FarObject<Structure> {

    public Long getId() {
        return null; // TODO: implement
    }

    public void setId(Long id) {
        // TODO: implement
    }

    public String getURI() {
        return null; // TODO: implement
    }

    public void setURI(String uri) {
        // TODO: implement
    }

    public int getMaxDepth() {
        return 0; // TODO: implement
    }

    public void setMaxDepth(int maxDepth) {
        // TODO: implement
    }

    public String toString() {
        return null; // TODO: implement
    }

    public void markExtensible(NodeDTO node) {
        // TODO: implement
    }

    public boolean equals(Object other) {
        return false; // TODO: implement
    }

    public Structure declareNearType() {
        return null; // TODO: implement
    }

}
