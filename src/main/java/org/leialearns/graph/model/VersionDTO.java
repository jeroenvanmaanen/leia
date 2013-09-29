package org.leialearns.graph.model;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.AgentMode;
import org.leialearns.graph.HasId;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.graph.session.SessionDTO;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.model.Version;
import org.leialearns.bridge.FarObject;
import org.leialearns.utilities.TypedIterable;
import java.io.Serializable;

public class VersionDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Version> {

    public Long getOrdinal() {
        return null; // TODO: implement
    }

    public Long getId() {
        return null; // TODO: implement
    }

    public void setId(Long id) {
        // TODO: implement
    }

    public ModelType getModelType() {
        return null; // TODO: implement
    }

    public void setModelType(ModelType modelType) {
        // TODO: implement
    }

    public void setModelTypeFlag(char modelTypeFlag) {
        // TODO: implement
    }

    public AccessMode getAccessMode() {
        return null; // TODO: implement
    }

    public void setAccessMode(AccessMode accessMode) {
        // TODO: implement
    }

    public void setAccessModeFlag(char accessModeFlag) {
        // TODO: implement
    }

    public InteractionContextDTO getInteractionContext() {
        return null; // TODO: implement
    }

    public void setInteractionContext(InteractionContextDTO interactionContext) {
        // TODO: implement
    }

    public SessionDTO getOwner() {
        return null; // TODO: implement
    }

    public void setOwner(SessionDTO owner) {
        // TODO: implement
    }

    public TypedIterable<SessionDTO> getWriters() {
        return null; // TODO: implement
    }

    public TypedIterable<SessionDTO> getReaders() {
        return null; // TODO: implement
    }

    public void registerWriter(SessionDTO writer, AgentMode agentMode) {
        // TODO: implement
    }

    public void registerReader(SessionDTO reader, AgentMode agentMode) {
        // TODO: implement
    }

    public Long getLogInterval() {
        return null; // TODO: implement
    }

    public int compareTo(VersionDTO version) {
        return 0; // TODO: implement
    }

    public boolean equals(Object other) {
        return false; // TODO: implement
    }

    public String toString() {
        return null; // TODO: implement
    }

    public Version declareNearType() {
        return null; // TODO: implement
    }

}
