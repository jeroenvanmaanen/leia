package org.leialearns.graph.model;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.HasId;
import org.leialearns.logic.model.Expected;
import java.io.Serializable;

public class ExpectedDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Expected> {

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

    public ModelType getModelType() {
        return null; // TODO: implement
    }

    public ToggledDTO getToggled() {
        return null; // TODO: implement
    }

    public void setToggled(ToggledDTO toggled) {
        // TODO: implement
    }

    public ObservedDTO getObserved() {
        return null; // TODO: implement
    }

    public void setObserved(ObservedDTO observed) {
        // TODO: implement
    }

    public String toString() {
        return null; // TODO: implement
    }

    public Expected declareNearType() {
        return null; // TODO: implement
    }

}
