package org.leialearns.graph.model;

import org.leialearns.api.enumerations.ModelType;
import org.leialearns.api.model.Expected;
import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.io.Serializable;

import static org.leialearns.graph.IdDaoSupport.toID;
import static org.leialearns.utilities.Display.displayParts;

@NodeEntity
public class ExpectedDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Expected> {
    @GraphId
    private Long id;

    @RelatedTo(direction = Direction.OUTGOING, type = "EXTENDS")
    private VersionDTO version;

    @RelatedTo(direction = Direction.OUTGOING, type = "REFERENCE_TOGGLED")
    private ToggledDTO toggled;

    @RelatedTo(direction = Direction.OUTGOING, type = "REFERENCE_OBSERVED")
    private ObservedDTO observed;

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

    public ModelType getModelType() {
        return ModelType.EXPECTED;
    }

    public ToggledDTO getToggled() {
        return toggled;
    }

    public void setToggled(ToggledDTO toggled) {
        this.toggled = toggled;
    }

    public ObservedDTO getObserved() {
        return observed;
    }

    public void setObserved(ObservedDTO observed) {
        this.observed = observed;
    }

    public String toString() {
        return displayParts("Expected", version, toID("T", toggled));
    }

    public Expected declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
