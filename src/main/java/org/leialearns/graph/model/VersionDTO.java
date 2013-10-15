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
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.leialearns.graph.IdDaoSupport.toID;
import static org.leialearns.utilities.Display.displayParts;
import static org.leialearns.utilities.Static.equal;

@NodeEntity
public class VersionDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Version> {
    @GraphId
    private Long id;

    @RelatedTo(direction = Direction.OUTGOING, type = "IN_CONTEXT")
    private InteractionContextDTO interactionContext;

    @RelatedTo(direction = Direction.OUTGOING, type = "OWNED_BY")
    private SessionDTO owner;

    private Long ordinal;
    private Character modelTypeFlag;
    private Character accessModeFlag;

    private transient Map<Long,SessionDTO> readers = new HashMap<Long,SessionDTO>();
    private transient Map<Long,SessionDTO> writers = new HashMap<Long,SessionDTO>();

    public Long getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Long ordinal) {
        if (this.ordinal != null) {
            throw new IllegalArgumentException("Ordinal is already set to: " + this.ordinal + ": (" + ordinal + ")");
        }
        this.ordinal = ordinal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModelType getModelType() {
        return ModelType.valueOf(modelTypeFlag);
    }

    public void setModelType(ModelType modelType) {
        modelTypeFlag = modelType.toChar();
    }

    public void setModelTypeFlag(char modelTypeFlag) {
        ModelType modelType = ModelType.valueOf(modelTypeFlag);
        if (modelType == null) {
            throw new IllegalArgumentException("Not a valid model type flag: [" + modelTypeFlag + "]");
        }
        this.modelTypeFlag = modelTypeFlag;
    }

    public AccessMode getAccessMode() {
        return AccessMode.valueOf(accessModeFlag);
    }

    public void setAccessMode(AccessMode accessMode) {
        accessModeFlag = accessMode.toChar();
    }

    public void setAccessModeFlag(char accessModeFlag) {
        AccessMode accessMode = AccessMode.valueOf(accessModeFlag);
        if (accessMode == null) {
            throw new IllegalArgumentException("Not a valid access mode flag: [" + accessModeFlag + "]");
        }
        this.accessModeFlag = accessModeFlag;
    }

    public InteractionContextDTO getInteractionContext() {
        return interactionContext;
    }

    public void setInteractionContext(InteractionContextDTO interactionContext) {
        this.interactionContext = interactionContext;
    }

    public SessionDTO getOwner() {
        return owner;
    }

    public void setOwner(SessionDTO owner) {
        this.owner = owner;
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

    @Override
    public boolean equals(Object other) {
        return other instanceof VersionDTO && equal(id, ((VersionDTO) other).getId());
    }

    @Override
    public String toString() {
        AccessMode accessMode = getAccessMode();
        char accessModeChar = accessMode == null ? '?' : accessMode.toChar();
        return displayParts("Version", id, "#" + (ordinal == null ? "?" : String.valueOf(ordinal)), getModelType(), toID("S", owner), accessModeChar, toID("I", interactionContext));
    }

    @Override
    public Version declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
