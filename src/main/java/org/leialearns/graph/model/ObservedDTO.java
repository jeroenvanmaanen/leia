package org.leialearns.graph.model;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.HasId;
import org.leialearns.logic.model.Observed;
import java.io.Serializable;

public class ObservedDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Observed> {

    public Long getId() {
        return null; // TODO: implement
    }

    public void setId(Long id) {
        // TODO: implement
    }

    public VersionDTO getVersion() {
        return null; // TODO: implement
    }

    public void setVersion(VersionDTO version) {
        // TODO: implement
    }

    public VersionDTO getCountedVersion() {
        return null; // TODO: implement
    }

    public void setCountedVersion(VersionDTO countedVersion) {
        // TODO: implement
    }

    public VersionDTO getDeltaVersion() {
        return null; // TODO: implement
    }

    public void setDeltaVersion(VersionDTO deltaVersion) {
        // TODO: implement
    }

    public ToggledDTO getToggled() {
        return null; // TODO: implement
    }

    public void setToggled(ToggledDTO toggled) {
        // TODO: implement
    }

    public ExpectedDTO getExpected() {
        return null; // TODO: implement
    }

    public void setExpected(ExpectedDTO expected) {
        // TODO: implement
    }

    public ModelType getModelType() {
        return null; // TODO: implement
    }

    public String toString() {
        return null; // TODO: implement
    }

    public Observed declareNearType() {
        return null; // TODO: implement
    }

}
