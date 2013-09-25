package org.leialearns.graph.structure;

import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.enumerations.Direction;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.utilities.TypedIterable;

public class StructureDAO extends IdDaoSupport<StructureDTO> {

    public StructureDTO find(String uri) {
        return null; // TODO: implement
    }

    public StructureDTO findOrCreate(String uri) {
        return null; // TODO: implement
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
