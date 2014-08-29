package org.leialearns.graph.interaction;

import com.google.common.base.Joiner;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.enumerations.Direction;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.model.VersionDTO;
import org.leialearns.graph.structure.StructureDAO;
import org.leialearns.graph.structure.StructureDTO;
import org.leialearns.graph.util.GraphLogger;
import org.leialearns.utilities.ExceptionWrapper;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.leialearns.utilities.Static.getLoggingClass;

@Transactional("neo4jTransactionManager")
public class InteractionContextDAO extends IdDaoSupport<InteractionContextDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    InteractionContextRepository repository;

    @Autowired
    private AlphabetDAO alphabetDAO;

    @Autowired
    private StructureDAO structureDAO;

    @Autowired
    private GraphLogger graphLogger;

    public InteractionContextDTO find(String uri) {
        InteractionContextDTO result = repository.getInteractionContextByUri(uri);
        logger.debug("Result: {}: {}", uri, result);
        return result;
    }

    public InteractionContextDTO findOrCreate(String uri) {
        InteractionContextDTO result;
        URL base;
        String actionsURI;
        String responsesURI;
        String structureURI;
        try {
            base = new URL(uri + "/.");
            actionsURI = new URL(base, "actions").toString();
            result = find(uri);
            if (result == null) {
                responsesURI = new URL(base, "responses").toString();
                structureURI = new URL(base, "structure").toString();
                logger.debug("Base URI: [{}]", base);
                logger.debug("Actions URI: [{}]", actionsURI);
                logger.debug("Responses URI: [{}]", responsesURI);
                logger.debug("Structure URI: [{}]", structureURI);
                AlphabetDTO actions = alphabetDAO.findOrCreate(actionsURI);
                AlphabetDTO responses = alphabetDAO.findOrCreate(responsesURI);
                StructureDTO structure = structureDAO.findOrCreate(structureURI);
                result = findOrCreate(uri, actions, responses, structure);
            }
        } catch (Throwable e) {
            throw ExceptionWrapper.wrap(e);
        }
        return result;
    }

    public InteractionContextDTO findOrCreate(String uri, AlphabetDTO actions, AlphabetDTO responses, StructureDTO structure) {
        if (uri == null) {
            throw new IllegalArgumentException("Argument URI should not be null");
        }
        InteractionContextDTO result = find(uri);
        if (result == null) {
            result = new InteractionContextDTO();
            result.setURI(uri);
            result.setActions(actions);
            result.setResponses(responses);
            result.setStructure(structure);
            result = repository.save(result);
            logNextVersions(result);
            logActions(result);
            repository.setEmptyVersionChain(result);
            logNextVersions(result);
            logActions(result);
            logger.debug("New interaction context: actions: {}", result.getActions());
        }
        logger.debug("Result: {}", result);
        return result;
    }

    private void logActions(InteractionContextDTO context) {
        logger.debug("Interaction context: actions: {}: [{}]", context, Joiner.on(", ").join(repository.getActions(context)));
    }

    private void logNextVersions(InteractionContextDTO context) {
        graphLogger.log("Interaction context", context, 3);
        Set<VersionDTO> versions = repository.getNextVersions(context);
        Long[] ids = new Long[versions.size()];
        Iterator<VersionDTO> it = versions.iterator();
        for (int i = 0; i < ids.length; i++) {
            ids[i] = it.next().getId();
        }
        logger.debug("Interaction context: next versions: {}: [{}]", context, Joiner.on(", ").join(ids));
    }

    @BridgeOverride
    public TypedIterable<DirectedSymbolDTO> createPath(InteractionContextDTO interactionContext, String... path) {
        Map<Direction,AlphabetDTO> map = new HashMap<>();
        map.put(Direction.ACTION, interactionContext.getActions());
        map.put(Direction.RESPONSE, interactionContext.getResponses());
        List<DirectedSymbolDTO> result = new ArrayList<>();
        for (String denotation : path) {
            Direction direction = Direction.valueOf(denotation.charAt(0));
            SymbolDTO symbol = alphabetDAO.internalize(map.get(direction), denotation.substring(1));
            result.add(symbol.createDirectedSymbol(direction));
        }
        return new TypedIterable<>(result, DirectedSymbolDTO.class);
    }

    public boolean equals(InteractionContextDTO interactionContext, Object other) {
        return interactionContext.equals(adapt(other, InteractionContextDTO.class));
    }

}