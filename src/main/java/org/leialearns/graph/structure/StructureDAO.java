package org.leialearns.graph.structure;

import org.leialearns.bridge.FarObject;
import org.leialearns.graph.KeyGraphNodeDAO;
import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.enumerations.Direction;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.logic.structure.Structure;
import org.leialearns.utilities.TypedIterable;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.leialearns.utilities.Static.getLoggingClass;

public class StructureDAO extends KeyGraphNodeDAO<StructureDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    public StructureDAO() {
        super("Structure", "uri");
    }

    public StructureDTO find(String uri) {
        return null; // TODO: implement
    }

    public StructureDTO findOrCreate(String uri) {
        Node structureNode = getOrCreate(uri);
        StructureDTO structureDTO = new StructureDTO();
        structureDTO.setGraphNode(structureNode);
        logger.debug("Alphabet: " + structureDTO.toString());
        return structureDTO;
    }

    public void updateMaxDepth(final StructureDTO structure, NodeDTO node) {
        // TODO: implement
    }

    public NodeDTO findOrCreateNode(StructureDTO structure, TypedIterable<DirectedSymbolDTO> path) {
        return null; // TODO: implement
    }

    public NodeDTO findOrCreateNode(StructureDTO structure, SymbolDTO symbol, Direction direction) {
        return null; // TODO: implement
    }

    public NodeDTO findOrCreateNode(StructureDTO structureDTO, SymbolDTO action, TypedIterable<DirectedSymbolDTO> path) {
        return null; // TODO: implement
    }

    public NodeDTO findOrCreateNode(StructureDTO structureDTO, DirectedSymbolDTO firstSymbol, TypedIterable<DirectedSymbolDTO> path) {
        return null; // TODO: implement
    }

    public void logNodes(StructureDTO structure) {
        // TODO: implement
    }

    public boolean equals(StructureDTO structure, Object other) {
        return false; // TODO: implement
    }

}
