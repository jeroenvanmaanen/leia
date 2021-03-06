package org.leialearns.graph.session;

import org.leialearns.api.session.Session;
import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.common.BaseSupplier;
import org.leialearns.graph.common.HasId;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.neo4j.graphdb.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.io.Serializable;

import static org.leialearns.common.Display.displayParts;
import static org.leialearns.common.Static.equal;
import static org.leialearns.common.Static.getLoggingClass;

@NodeEntity
public class SessionDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Session> {
    private final transient Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @GraphId
    private Long id;
    private transient RootDTO root;

    @RelatedTo(direction = Direction.OUTGOING, type = "IN_CONTEXT")
    @Fetch private InteractionContextDTO interactionContext;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RootDTO getRoot() {
        return root;
    }

    public void setRoot(RootDTO root) {
        this.root = root;
    }

    public InteractionContextDTO getInteractionContext() {
        return interactionContext;
    }

    public void setInteractionContext(InteractionContextDTO interactionContext) {
        this.interactionContext = interactionContext;
    }

    public String toString() {
        return displayParts("Session", id, interactionContext);
    }

    public boolean equals(final Object other) {
        logger.trace("Equals?: {}: {}: {}", new Object[]{id, other instanceof SessionDTO, new BaseSupplier<String>() {
            public String get() {
                String result;
                if (other instanceof SessionDTO) {
                    Long id = ((SessionDTO) other).getId();
                    result = String.valueOf(id);
                } else {
                    result = "?";
                }
                return result;
            }
        }});
        boolean result = other instanceof SessionDTO && equal(((SessionDTO) other).getId(), id);
        logger.trace("Result: {}", result);
        return result;
    }

    public Session declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
