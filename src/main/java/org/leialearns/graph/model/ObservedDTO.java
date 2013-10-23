package org.leialearns.graph.model;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.HasId;
import org.leialearns.logic.model.Observed;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.io.Serializable;

import static org.leialearns.graph.IdDaoSupport.toID;
import static org.leialearns.utilities.Display.displayParts;

@NodeEntity
public class ObservedDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Observed> {
    @GraphId
    private Long id;

    @RelatedTo(direction = Direction.OUTGOING, type = "EXTENDS")
    private VersionDTO version;

    @RelatedTo(direction = Direction.OUTGOING, type = "HAS_COUNTED")
    private VersionDTO countedVersion;

    @RelatedTo(direction = Direction.OUTGOING, type = "HAS_DELTA")
    private VersionDTO deltaVersion;

    @RelatedTo(direction = Direction.OUTGOING, type = "HAS_TOGGLED")
    private ToggledDTO toggled;

    @RelatedTo(direction = Direction.OUTGOING, type = "HAS_EXPECTED")
    private ExpectedDTO expected;

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

    public VersionDTO getCountedVersion() {
        return countedVersion;
    }

    public void setCountedVersion(VersionDTO countedVersion) {
        this.countedVersion = countedVersion;
    }

    public VersionDTO getDeltaVersion() {
        return deltaVersion;
    }

    public void setDeltaVersion(VersionDTO deltaVersion) {
        this.deltaVersion = deltaVersion;
    }

    public ToggledDTO getToggled() {
        return toggled;
    }

    public void setToggled(ToggledDTO toggled) {
        this.toggled = toggled;
    }

    public ExpectedDTO getExpected() {
        return expected;
    }

    public void setExpected(ExpectedDTO expected) {
        this.expected = expected;
    }

    public ModelType getModelType() {
        return ModelType.OBSERVED;
    }

    public String toString() {
        return displayParts("Observed", toID("O", version), toID("C", countedVersion), toID("D", deltaVersion), toID("E", expected), toID("T", toggled));
    }

    public Observed declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
