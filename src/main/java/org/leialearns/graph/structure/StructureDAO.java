package org.leialearns.graph.structure;

import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.enumerations.Direction;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.repositories.StructureRepository;
import org.leialearns.logic.structure.Structure;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.leialearns.bridge.Static.getFarObject;
import static org.leialearns.utilities.Display.asDisplay;
import static org.leialearns.utilities.Static.getLoggingClass;

@Transactional("neo4jTransactionManager")
public class StructureDAO {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private NodeDAO nodeDAO;

    @Autowired
    StructureRepository repository;

    public StructureDTO find(String uri) {
        return repository.getStructureByUri(uri);
    }

    public StructureDTO findOrCreate(String uri) {
        StructureDTO structureDTO = find(uri);
        if (structureDTO == null) {
            structureDTO = new StructureDTO();
            structureDTO.setURI(uri);
            structureDTO = repository.save(structureDTO);
            logger.debug("New structure: [{}]", asDisplay(structureDTO));
        } else {
            logger.debug("Found structure: [{}]", asDisplay(structureDTO));
        }
        return structureDTO;
    }

    public void updateMaxDepth(final StructureDTO structure, NodeDTO node) {
        // TODO: implement
    }

    public NodeDTO findOrCreateNode(StructureDTO structure, TypedIterable<DirectedSymbolDTO> path) {
        logger.trace("Before find or create node");
        Iterator<DirectedSymbolDTO> it = path.iterator();
        NodeDTO node;
        if (it.hasNext()) {
            DirectedSymbolDTO directedSymbol = it.next();
            node = findOrCreateNode(structure, directedSymbol, path);
        } else {
            throw new IllegalArgumentException("Empty path");
        }
        return node;
    }

    public NodeDTO findOrCreateNode(StructureDTO structure, SymbolDTO symbol, Direction direction) {
        List<DirectedSymbolDTO> empty = new ArrayList<DirectedSymbolDTO>(0);
        TypedIterable<DirectedSymbolDTO> iterable = new TypedIterable<DirectedSymbolDTO>(empty, DirectedSymbolDTO.class);
        return findOrCreateNode(structure, symbol.createDirectedSymbol(direction), iterable);
    }

    public NodeDTO findOrCreateNode(StructureDTO structureDTO, SymbolDTO action, TypedIterable<DirectedSymbolDTO> path) {
        return findOrCreateNode(structureDTO, action.createDirectedSymbol(Direction.ACTION), path);
    }

    public NodeDTO findOrCreateNode(StructureDTO structureDTO, DirectedSymbolDTO firstSymbol, TypedIterable<DirectedSymbolDTO> path) {
        logger.trace("Before find or create node with first symbol");
        logger.trace("Base type of path: [{}]", asDisplay(path.getType()));
        logger.trace("First symbol: [{}]", asDisplay(firstSymbol));
        Iterator<DirectedSymbolDTO> it = path.iterator();
        NodeDTO nodeDTO = nodeDAO.findOrCreate(structureDTO, firstSymbol.getSymbol(), firstSymbol.getDirection());
        while (nodeDTO.getExtensible() && it.hasNext()) {
            DirectedSymbolDTO directedSymbol = it.next();
            logger.trace("Directed symbol: " + directedSymbol);
            nodeDTO = nodeDAO.findOrCreate(nodeDTO, directedSymbol.getSymbol(), directedSymbol.getDirection());
        }
        int newMaxDepth = nodeDTO.getStructure().getMaxDepth();
        if (newMaxDepth > structureDTO.getMaxDepth()) {
            structureDTO.setMaxDepth(newMaxDepth);
        }
        return nodeDTO;
    }

    public void logNodes(StructureDTO structure) {
        // TODO: implement
    }

    public boolean equals(StructureDTO structure, Object other) {
        Object otherObject = (other instanceof Structure ? getFarObject((Structure) other, StructureDTO.class) : other);
        return structure.equals(otherObject);
    }

}
