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
import static org.leialearns.utilities.Static.equal;

@NodeEntity
public class StructureDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Structure> {
    @GraphId
    private Long id;

    @Indexed(unique = true)
    private String uri;

    private int maxDepth;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
        return displayParts("Structure", id, uri, maxDepth);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof StructureDTO && equal(uri, ((StructureDTO) other).getURI());
    }

    @Override
    public int hashCode() {
        return uri == null ? 0 : uri.hashCode();
    }

    @Override
    public Structure declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
