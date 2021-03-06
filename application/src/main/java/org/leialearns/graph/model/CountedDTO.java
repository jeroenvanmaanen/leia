package org.leialearns.graph.model;

import org.leialearns.api.enumerations.ModelType;
import org.leialearns.api.model.Counted;
import org.leialearns.bridge.FarObject;
import org.leialearns.common.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.leialearns.bridge.Static.getFarObject;
import static org.leialearns.common.Display.displayParts;
import static org.leialearns.common.Static.equal;
import static org.leialearns.common.Static.getLoggingClass;

public class CountedDTO implements FarObject<Counted> {
    private final transient Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Setting<VersionDTO> version = new Setting<>("Version");

    public VersionDTO getVersion() {
        return version.get();
    }

    public void setVersion(VersionDTO version) {
        ModelType modelType = version.getModelType();
        if (modelType != getModelType()) {
            throw new IllegalArgumentException("Model type inconsistent with class: " + modelType + ": " + getClass().getSimpleName());
        }
        this.version.set(version);
    }

    public ModelType getModelType() {
        return ModelType.COUNTED;
    }

    public String toString() {
        return displayParts("Counted", (version.isFixated() ? version.get() : null));
    }

    public boolean equals(Object other) {
        logger.trace("Equals? " + this + " =?= " + other);
        CountedDTO otherCounted;
        if (other instanceof CountedDTO) {
            otherCounted = (CountedDTO) other;
        } else if (other instanceof Counted) {
            otherCounted = getFarObject((Counted) other, CountedDTO.class);
        } else {
            otherCounted = null;
        }
        VersionDTO otherVersion = (otherCounted == null ? null : otherCounted.getVersion());
        logger.trace("Equals? " + getVersion() + " =?= " + otherVersion);
        boolean result = equal(getVersion(), otherVersion);
        logger.trace("Result: " + result);
        return result;
    }

    public Counted declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
