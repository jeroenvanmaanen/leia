package org.leialearns.graph.session;

import org.leialearns.bridge.BridgeFactory;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.interaction.AlphabetDTO;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.logic.session.Root;
import org.leialearns.utilities.TypedIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RootDTO implements FarObject<Root> {

    @Autowired
    @Qualifier(value = "rootFactory")
    private BridgeFactory rootFactory;


    public InteractionContextDTO createInteractionContext(String interactionContextURI) {
        return null; // TODO: implement
    }

    public InteractionContextDTO createInteractionContext(String interactionContextURI, String actionsURI, String responsesURI, String structureURI) {
        return null; // TODO: implement
    }

    public SessionDTO createSession(String interactionContextURI) {
        return null; // TODO: implement
    }

    public SessionDTO createSession(InteractionContextDTO interactionContext) {
        return null; // TODO: implement
    }

    public TypedIterable<AlphabetDTO> findAlphabets() {
        return null; // TODO: implement
    }

    public Root declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

    public Root getNearObject() {
        return (Root) rootFactory.getNearObject(this);
    }

}
