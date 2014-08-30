package org.leialearns.graph.interaction;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.graph.model.VersionDTO;
import org.leialearns.logic.interaction.InteractionContext;
import org.leialearns.graph.structure.StructureDTO;
import org.neo4j.graphdb.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
// import org.springframework.data.neo4j.annotation.Indexed; // TODO: remove
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.io.Serializable;

import static org.leialearns.utilities.Display.displayParts;

@NodeEntity
@TypeAlias("InteractionContext")
public class InteractionContextDTO extends BaseBridgeFacet implements HasId, Serializable, Comparable<InteractionContextDTO>, FarObject<InteractionContext> {
    private static final Logger logger = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());

    @GraphId
    private Long id;

    // @Indexed(unique = true) // TODO: remove
    @Fetch private String uri;

    @RelatedTo(direction = Direction.OUTGOING, type = "HAS_ACTIONS")
    @Fetch private AlphabetDTO actions;

    @RelatedTo(direction = Direction.OUTGOING, type = "HAS_RESPONSES")
    @Fetch private AlphabetDTO responses;

    @RelatedTo(direction = Direction.OUTGOING, type = "HAS_STRUCTURE")
    @Fetch private StructureDTO structure;

    @RelatedTo(direction = Direction.OUTGOING, type = "LATEST_VERSION")
    @Fetch private VersionDTO latestVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public AlphabetDTO getActions() {
        AlphabetDTO myActions;
        try {
            myActions = actions;
        } catch (IllegalArgumentException exception) {
            myActions = null;
        }
        logger.debug("Get actions: {}: {}: {}", new Object[] { id, uri, myActions });
        return actions;
    }

    public void setActions(AlphabetDTO actions) {
        this.actions = actions;
    }

    public AlphabetDTO getResponses() {
        return responses;
    }

    public void setResponses(AlphabetDTO responses) {
        this.responses = responses;
    }

    public StructureDTO getStructure() {
        return structure;
    }

    public void setStructure(StructureDTO structure) {
        this.structure = structure;
    }

    public VersionDTO getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(VersionDTO latestVersion) {
        this.latestVersion = latestVersion;
    }

    public int compareTo(InteractionContextDTO interactionContext) {
        return this.uri.compareTo(interactionContext.getURI());
    }

    @Override
    public boolean equals(Object other) {
        boolean result;
        if (other instanceof InteractionContextDTO) {
            InteractionContextDTO otherDTO = (InteractionContextDTO) other;
            if (id != null && id.equals(otherDTO.getId())) {
                result = true;
            } else {
                result = uri != null && uri.equals(((InteractionContextDTO) other).getURI());
            }
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public String toString() {
        return displayParts("InteractionContext", id, uri);
    }

    @Override
    public InteractionContext declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
