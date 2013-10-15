package org.leialearns.graph.model;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.HasId;
import org.leialearns.logic.model.Toggled;
import org.leialearns.graph.structure.NodeDTO;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.io.Serializable;

import static org.leialearns.graph.IdDaoSupport.toID;
import static org.leialearns.utilities.Display.displayParts;
import static org.leialearns.utilities.Static.equal;

@NodeEntity
public class ToggledDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Toggled> {
    @GraphId
    private Long id;

    @RelatedTo(direction = Direction.OUTGOING, type = "EXTENDS")
    private VersionDTO version;

    @RelatedTo(direction = Direction.OUTGOING, type = "FOR_NODE")
    private NodeDTO node;

    @RelatedTo(direction = Direction.OUTGOING, type = "REFERENCE_EXPECTED")
    private ExpectedDTO expected;

    @RelatedTo(direction = Direction.OUTGOING, type = "REFERENCE_OBSERVED")
    private ObservedDTO observed;

    private Boolean include;

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
