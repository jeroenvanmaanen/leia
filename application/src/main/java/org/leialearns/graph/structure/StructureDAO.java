package org.leialearns.graph.structure;

import org.leialearns.api.enumerations.Direction;
import org.leialearns.api.structure.Structure;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.common.TypedIterable;
import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.graph.interaction.SymbolDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.leialearns.bridge.Static.getFarObject;
import static org.leialearns.common.Display.asDisplay;
import static org.leialearns.common.Static.getLoggingClass;

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
        if (!node.getStructure().equals(structure)) {
            throw new IllegalArgumentException("Node does not belong to structure: [" + node + "]: [" + structure + "]");
        }
        final int nodeDepth = node.getDepth();
        logger.trace("Update max depth: [{}]: {}: [{}]: {}", new Object[] { structure, structure.getMaxDepth(), node, nodeDepth });
        if (nodeDepth > structure.getMaxDepth()) {
            Integer newNodeDepth = repository.updateMaxDepth(structure, nodeDepth);
            StructureDTO updatedStructure = repository.findOne(structure.getId());
            logger.trace("Updated max depth: {}: {}", updatedStructure, newNodeDepth);
            if (updatedStructure.getMaxDepth() < nodeDepth) {
                RuntimeException exception = new IllegalStateException("Structure max depth less than node depth: " + updatedStructure.getMaxDepth() + ": " + nodeDepth);
                logger.warn("Exception", exception);
                throw exception;
            }
        }
    }

    @BridgeOverride
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

    @BridgeOverride
    public NodeDTO findOrCreateNode(StructureDTO structure, SymbolDTO symbol, Direction direction) {
        List<DirectedSymbolDTO> empty = new ArrayList<>(0);
        TypedIterable<DirectedSymbolDTO> iterable = new TypedIterable<>(empty, DirectedSymbolDTO.class);
        return findOrCreateNode(structure, symbol.createDirectedSymbol(direction), iterable);
    }

    @BridgeOverride
    public NodeDTO findOrCreateNode(StructureDTO structure, SymbolDTO action, TypedIterable<DirectedSymbolDTO> path) {
        return findOrCreateNode(structure, action.createDirectedSymbol(Direction.ACTION), path);
    }

    public NodeDTO findOrCreateNode(StructureDTO structure, DirectedSymbolDTO firstSymbol, TypedIterable<DirectedSymbolDTO> path) {
        logger.trace("Before find or create node with first symbol");
        logger.trace("Base type of path: [{}]", asDisplay(path.getType()));
        logger.trace("First symbol: [{}]", asDisplay(firstSymbol));
        Iterator<DirectedSymbolDTO> it = path.iterator();
        NodeDTO node = nodeDAO.findOrCreate(structure, firstSymbol.getSymbol(), firstSymbol.getDirection());
        logger.trace("First node: [{}]", asDisplay(node));
        while (node.getExtensible() && it.hasNext()) {
            DirectedSymbolDTO directedSymbol = it.next();
            logger.trace("Directed symbol: " + directedSymbol);
            node = nodeDAO.findOrCreate(node, directedSymbol.getSymbol(), directedSymbol.getDirection());
            logger.trace("Next node: [{}]", asDisplay(node));
        }
        int newMaxDepth = node.getStructure().getMaxDepth();
        if (newMaxDepth > structure.getMaxDepth()) {
            structure.setMaxDepth(newMaxDepth);
        }
        return node;
    }

    @BridgeOverride
    public void logNodes(StructureDTO structure) {
        if (logger.isInfoEnabled()) {
            SortedSet<String> nodes = new TreeSet<>();
            for (NodeDTO node : nodeDAO.findNodes(structure)) {
                nodes.add(nodeDAO.toString(node));
            }
            logger.info("Structure: [" + this + "]: {");
            for (String node : nodes) {
                logger.info("  " + node);
            }
            logger.info("}");
        }
    }

    public boolean equals(StructureDTO structure, Object other) {
        Object otherObject = (other instanceof Structure ? getFarObject((Structure) other, StructureDTO.class) : other);
        return structure.equals(otherObject);
    }

}
