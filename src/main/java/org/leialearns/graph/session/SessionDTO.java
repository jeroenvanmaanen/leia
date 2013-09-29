package org.leialearns.graph.session;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.logic.session.Session;
import java.io.Serializable;

public class SessionDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Session> {

    public Long getId() {
        return null; // TODO: implement
    }

    public void setId(Long id) {
        // TODO: implement
    }

    public RootDTO getRoot() {
        return null; // TODO: implement
    }

    public void setRoot(RootDTO root) {
        // TODO: implement
    }

    public InteractionContextDTO getInteractionContext() {
        return null; // TODO: implement
    }

    public void setInteractionContext(InteractionContextDTO interactionContext) {
        // TODO: implement
    }

    public String toString() {
        return null; // TODO: implement
    }

    public Session declareNearType() {
        return null; // TODO: implement
    }

    public boolean equals(Object other) {
        return false; // TODO: implement
    }

}
