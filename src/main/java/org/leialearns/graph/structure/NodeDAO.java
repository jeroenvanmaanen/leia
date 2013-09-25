package org.leialearns.graph.structure;

import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.enumerations.Direction;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.utilities.TypedIterable;

public class NodeDAO extends IdDaoSupport<NodeDTO> {

    public TypedIterable<NodeDTO> findAll() {
        return null; // TODO: implement
    }

    public TypedIterable<NodeDTO> findNodes(StructureDTO structure) {
        return null; // TODO: implement
    }

    public TypedIterable<NodeDTO> findNodes(StructureDTO structure, int depth) {
        return null; // TODO: implement
    }

    public TypedIterable<NodeDTO> findRootNodes(StructureDTO structure) {
        return null; // TODO: implement
    }

    public TypedIterable<NodeDTO> findChildren(NodeDTO node) {
        return null; // TODO: implement
    }

    public NodeDTO find(StructureDTO structure, TypedIterable<DirectedSymbolDTO> path) {
        return null; // TODO: implement
    }

    public NodeDTO findOrCreate(StructureDTO structure, SymbolDTO symbol, Direction direction) {
        return null; // TODO: implement
    }

    public NodeDTO findOrCreate(NodeDTO parent, SymbolDTO symbol, Direction direction) {
        return null; // TODO: implement
    }

    public boolean equals(NodeDTO node, Object other) {
        return false; // TODO: implement
    }

    public int compareTo(NodeDTO node, NodeDTO other) {
        return 0; // TODO: implement
    }

}
