package org.leialearns.graph.session;

import org.leialearns.bridge.BridgeFactory;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.interaction.AlphabetDAO;
import org.leialearns.graph.interaction.AlphabetDTO;
import org.leialearns.graph.interaction.InteractionContextDAO;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.graph.structure.StructureDAO;
import org.leialearns.graph.structure.StructureDTO;
import org.leialearns.logic.session.NeedsRoot;
import org.leialearns.logic.session.Root;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RootDTO implements FarObject<Root> {
    private static final Logger LOGGER = LoggerFactory.getLogger(new Object() {}.getClass().getEnclosingClass());
    private final NeedsRoot[] needsRootItems;

    @Autowired
    private InteractionContextDAO interactionContextDAO;

    @Autowired
    private AlphabetDAO alphabetDAO;

    @Autowired
    private StructureDAO structureDAO;

    @Autowired
    private SessionDAO sessionDAO;

    @Autowired
    @Qualifier(value = "rootFactory")
    private BridgeFactory rootFactory;

    public RootDTO() {
        this(null);
    }

    public RootDTO(NeedsRoot[] needsRootItems) {
        this.needsRootItems = needsRootItems;
    }

    @BridgeOverride
    public InteractionContextDTO createInteractionContext(String interactionContextURI) {
        return interactionContextDAO.findOrCreate(interactionContextURI);
    }

    @BridgeOverride
    public InteractionContextDTO createInteractionContext(String interactionContextURI, String actionsURI, String responsesURI, String structureURI) {
        AlphabetDTO actions = alphabetDAO.findOrCreate(actionsURI);
        AlphabetDTO responses = alphabetDAO.findOrCreate(responsesURI);
        StructureDTO structure = structureDAO.findOrCreate(structureURI);
        return interactionContextDAO.findOrCreate(interactionContextURI, actions, responses, structure);
    }

    public SessionDTO createSession(String interactionContextURI) {
        InteractionContextDTO interactionContext = interactionContextDAO.findOrCreate(interactionContextURI);
        LOGGER.debug("InteractionContext: {}", interactionContext);
        return createSession(interactionContext);
    }

    public SessionDTO createSession(InteractionContextDTO interactionContext) {
        return sessionDAO.create(this, interactionContext);
    }

    @BridgeOverride
    public TypedIterable<AlphabetDTO> findAlphabets() {
        return alphabetDAO.findAll();
    }

    public Root declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

    public Root getNearObject() {
        Root result = (Root) rootFactory.getNearObject(this);
        if (needsRootItems != null) {
            for (NeedsRoot needsRoot : needsRootItems) {
                needsRoot.setRoot(result);
            }
        }
        return result;
    }

}
