package org.leialearns.graph.interaction;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
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
    private String uri;

    @Fetch
    @RelatedTo(direction = Direction.OUTGOING, type = "HAS_ACTIONS", elementClass = AlphabetDTO.class)
    private AlphabetDTO actions;

    @Fetch
    @RelatedTo(direction = Direction.OUTGOING, type = "HAS_RESPONSES")
    private AlphabetDTO responses;

    @Fetch
    @RelatedTo(direction = Direction.OUTGOING, type = "HAS_STRUCTURE")
    private StructureDTO structure;

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


    public int compareTo(InteractionContextDTO interactionContext) {
        return this.uri.compareTo(interactionContext.getURI());
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof InteractionContextDTO && uri.equals(((InteractionContextDTO) other).getURI());
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