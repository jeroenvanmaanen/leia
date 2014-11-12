package org.leialearns.graph.model;

import org.leialearns.api.enumerations.AccessMode;
import org.leialearns.api.enumerations.ModelType;
import org.leialearns.api.model.Version;
import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.graph.session.SessionDTO;
import org.leialearns.utilities.ExceptionWrapper;
import org.leialearns.utilities.Setting;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.io.Serializable;

import static org.leialearns.graph.IdDaoSupport.toID;
import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Display.displayParts;
import static org.leialearns.utilities.Static.equal;

// import org.leialearns.api.enumerations.AgentMode;
// import org.leialearns.utilities.TypedIterable;
// import java.util.HashMap;
// import java.util.Map;

@NodeEntity
@TypeAlias("Version")
public class VersionDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Version> {
    private final transient Setting<Long> logInterval = new Setting<>("Log interval", 5 * 60 * 1000L);

    @GraphId
    private Long id;

    @RelatedTo(direction = Direction.OUTGOING, type = "IN_CONTEXT")
    @Fetch private InteractionContextDTO interactionContext;

    @RelatedTo(direction = Direction.OUTGOING, type = "OWNED_BY")
    @Fetch private SessionDTO owner;

    @RelatedTo(direction = Direction.OUTGOING, type = "NEXT_VERSION")
    @Fetch private VersionDTO nextVersion;

    @Fetch private Long ordinal;
    @Fetch private Character modelTypeFlag;
    @Fetch private Character accessModeFlag;

    /*
    private transient Map<Long,SessionDTO> readers = new HashMap<>();
    private transient Map<Long,SessionDTO> writers = new HashMap<>();
    */

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
        return modelTypeFlag == null ? null : ModelType.valueOf(modelTypeFlag);
    }

    public void setModelType(ModelType modelType) {
        modelTypeFlag = modelType.toChar();
    }

    @BridgeOverride
    public void setModelTypeFlag(char modelTypeFlag) {
        ModelType modelType = ModelType.valueOf(modelTypeFlag);
        if (modelType == null) {
            throw new IllegalArgumentException("Not a valid model type flag: [" + modelTypeFlag + "]");
        }
        this.modelTypeFlag = modelTypeFlag;
    }

    public AccessMode getAccessMode() {
        return accessModeFlag == null ? null : AccessMode.valueOf(accessModeFlag);
    }

    public void setAccessMode(AccessMode accessMode) {
        accessModeFlag = accessMode.toChar();
    }

    @BridgeOverride
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

    @SuppressWarnings("unused")
    public VersionDTO getNextVersion() {
        return nextVersion;
    }

    public void setNextVersion(VersionDTO nextVersion) {
        this.nextVersion = nextVersion;
    }

/*
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
    */

    public Long getLogInterval() {
        return logInterval.get();
    }

    public int compareTo(VersionDTO version) {
        Long versionId = version.getId();
        int result;
        try {
            result = id.compareTo(versionId);
        } catch (RuntimeException e) {
            throw ExceptionWrapper.wrap(e);
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof VersionDTO && equal(getId(), ((VersionDTO) other).getId());
    }

    public int hashCode() {
        Long id = getId();
        if (id == null) {
            throw new IllegalStateException("Version has no id yet: " + display(this));
        }
        return id.intValue();
    }

    @Override
    public String toString() {
        AccessMode accessMode = getAccessMode();
        char accessModeChar = accessMode == null ? '?' : accessMode.toChar();
        Long id = getId();
        Long ordinal = getOrdinal();
        SessionDTO owner = getOwner();
        InteractionContextDTO context = getInteractionContext();
        return displayParts("Version", id, "#" + (ordinal == null ? "?" : String.valueOf(ordinal)), getModelType(), toID("S", owner), accessModeChar, toID("I", context));
    }

    @Override
    public Version declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
