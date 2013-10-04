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
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public Long getId() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setId(Long id) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public ModelType getModelType() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setModelType(ModelType modelType) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setModelTypeFlag(char modelTypeFlag) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public AccessMode getAccessMode() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setAccessMode(AccessMode accessMode) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setAccessModeFlag(char accessModeFlag) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public InteractionContextDTO getInteractionContext() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setInteractionContext(InteractionContextDTO interactionContext) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public SessionDTO getOwner() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setOwner(SessionDTO owner) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<SessionDTO> getWriters() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<SessionDTO> getReaders() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void registerWriter(SessionDTO writer, AgentMode agentMode) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void registerReader(SessionDTO reader, AgentMode agentMode) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public Long getLogInterval() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public int compareTo(VersionDTO version) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public boolean equals(Object other) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public String toString() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public Version declareNearType() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

}
