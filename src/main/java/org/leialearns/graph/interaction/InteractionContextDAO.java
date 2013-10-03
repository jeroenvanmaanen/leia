package org.leialearns.graph.interaction;

import org.leialearns.graph.repositories.InteractionContextRepository;
import org.leialearns.graph.structure.StructureDAO;
import org.leialearns.graph.structure.StructureDTO;
import org.leialearns.utilities.ExceptionWrapper;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;

import static org.leialearns.utilities.Display.asDisplay;
import static org.leialearns.utilities.Static.getLoggingClass;

@Transactional("neo4jTransactionManager")
public class InteractionContextDAO {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    InteractionContextRepository repository;

    @Autowired
    private AlphabetDAO alphabetDAO;

    @Autowired
    private StructureDAO structureDAO;

    public TypedIterable<InteractionContextDTO> findAll() {
        return null; // TODO: implement
    }

    public InteractionContextDTO find(String uri) {
        return null; // TODO: implement
    }

    public InteractionContextDTO findOrCreate(String uri) {
        URL base;
        String actionsURI;
        String responsesURI;
        String structureURI;
        try {
            base = new URL(uri + "/.");
            actionsURI = new URL(base, "actions").toString();
            responsesURI = new URL(base, "responses").toString();
            structureURI = new URL(base, "structure").toString();
            logger.debug("Base URI: [{}]", base);
            logger.debug("Actions URI: [{}]", actionsURI);
            logger.debug("Responses URI: [{}]", responsesURI);
            logger.debug("Structure URI: [{}]", structureURI);
        } catch (Throwable e) {
            throw ExceptionWrapper.wrap(e);
        }

        AlphabetDTO actions = alphabetDAO.findOrCreate(actionsURI);
        AlphabetDTO responses = alphabetDAO.findOrCreate(responsesURI);
        StructureDTO structure = structureDAO.findOrCreate(structureURI);
        return findOrCreate(uri, actions, responses, structure);
    }

    public InteractionContextDTO findOrCreate(String uri, AlphabetDTO actions, AlphabetDTO responses, StructureDTO structure) {
        if (uri == null) {
            throw new IllegalArgumentException("Argument URI should not be null");
        }
        InteractionContextDTO result = repository.getInteractionContextByUri(uri);
        if (result == null) {
            result = new InteractionContextDTO();
            result.setURI(uri);
            result.setActions(actions);
            result.setResponses(responses);
            result.setStructure(structure);
            result = repository.save(result);
        }
        return result;
    }

    public void setActions(InteractionContextDTO interactionContextDTO, AlphabetDTO actions) {
        // TODO: implement
    }

    public void setResponses(InteractionContextDTO interactionContextDTO, AlphabetDTO responses) {
        // TODO: implement
    }

    public TypedIterable<DirectedSymbolDTO> createPath(InteractionContextDTO interactionContext, String... path) {
        return null; // TODO: implement
    }

    public void save(AlphabetDTO alphabet) {
        // TODO: implement
    }

    public boolean equals(InteractionContextDTO interactionContext, Object other) {
        return false; // TODO: implement
    }

}
