package org.leialearns.graph.model;

import org.leialearns.api.enumerations.ModelType;
import org.leialearns.api.model.Toggled;
import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.common.HasId;
import org.leialearns.graph.structure.NodeDTO;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.io.Serializable;

import static org.leialearns.graph.common.IdDaoSupport.toID;
import static org.leialearns.common.Display.displayParts;
import static org.leialearns.common.Static.equal;

@NodeEntity
public class ToggledDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Toggled> {
    @GraphId
    private Long id;

    @RelatedTo(direction = Direction.OUTGOING, type = "EXTENDS")
    @Fetch private VersionDTO version;

    @RelatedTo(direction = Direction.OUTGOING, type = "FOR_NODE")
    @Fetch private NodeDTO node;

    @RelatedTo(direction = Direction.OUTGOING, type = "REFERENCE_EXPECTED")
    @Fetch private ExpectedDTO expected;

    @RelatedTo(direction = Direction.OUTGOING, type = "REFERENCE_OBSERVED")
    @Fetch private ObservedDTO observed;

    @Fetch private Boolean include;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VersionDTO getVersion() {
        return version;
    }

    public void setVersion(VersionDTO version) {
        this.version = version;
    }

    public NodeDTO getNode() {
        return node;
    }

    public void setNode(NodeDTO node) {
        this.node = node;
    }

    public ExpectedDTO getExpected() {
        return expected;
    }

    public void setExpected(ExpectedDTO expected) {
        this.expected = expected;
    }

    public ObservedDTO getObserved() {
        return observed;
    }

    public void setObserved(ObservedDTO observed) {
        this.observed = observed;
    }

    public Boolean getInclude() {
        return include;
    }

    public void setInclude(Boolean include) {
        this.include = include;
    }

    public ModelType getModelType() {
        return ModelType.TOGGLED;
    }

    public String toString() {
        return displayParts("Toggled", version, node, include, toID("E", expected));
    }

    public boolean equals(Object other) {
        return other instanceof ToggledDTO && equal(id, ((ToggledDTO) other).getId());
    }

    public Toggled declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
