package org.leialearns.graph.session;

import org.leialearns.bridge.BridgeFactory;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.interaction.AlphabetDAO;
import org.leialearns.graph.interaction.AlphabetDTO;
import org.leialearns.graph.interaction.InteractionContextDAO;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.graph.structure.StructureDAO;
import org.leialearns.graph.structure.StructureDTO;
import org.leialearns.logic.session.Root;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.leialearns.utilities.Display.asDisplay;
import static org.leialearns.utilities.Static.getLoggingClass;

public class RootDTO implements FarObject<Root> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private InteractionContextDAO interactionContextDAO;

    @Autowired
    private AlphabetDAO alphabetDAO;

    @Autowired
    private StructureDAO structureDAO;

    @Autowired
    @Qualifier(value = "rootFactory")
    private BridgeFactory rootFactory;


    public InteractionContextDTO createInteractionContext(String interactionContextURI) {
        return interactionContextDAO.findOrCreate(interactionContextURI);
    }

    public InteractionContextDTO createInteractionContext(String interactionContextURI, String actionsURI, String responsesURI, String structureURI) {
        AlphabetDTO actions = alphabetDAO.findOrCreate(actionsURI);
        AlphabetDTO responses = alphabetDAO.findOrCreate(responsesURI);
        StructureDTO structure = structureDAO.findOrCreate(structureURI);
        return interactionContextDAO.findOrCreate(interactionContextURI, actions, responses, structure);
    }

    public SessionDTO createSession(String interactionContextURI) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public SessionDTO createSession(InteractionContextDTO interactionContext) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<AlphabetDTO> findAlphabets() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public Root declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

    public Root getNearObject() {
        return (Root) rootFactory.getNearObject(this);
    }

}
