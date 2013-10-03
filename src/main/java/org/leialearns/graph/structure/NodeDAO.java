package org.leialearns.graph.structure;

import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.enumerations.Direction;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.repositories.NodeRepository;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.utilities.Static.equal;
import static org.leialearns.utilities.Static.getLoggingClass;

public class NodeDAO extends IdDaoSupport<NodeDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private StructureDAO structureDAO;

    @Autowired
    private NodeRepository nodeRepository;

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
        StringBuilder builder = new StringBuilder();
        appendToPath(builder, symbol, direction);
        return findOrCreate(structure, null, builder.toString(), 1, symbol, direction);
    }

    public NodeDTO findOrCreate(NodeDTO parent, SymbolDTO symbol, Direction direction) {
        StringBuilder builder = new StringBuilder(parent.getPath());
        appendToPath(builder, symbol, direction);
        return findOrCreate(parent.getStructure(), parent, builder.toString(), parent.getDepth() + 1, symbol, direction);
    }

    protected NodeDTO findOrCreate(StructureDTO structure, NodeDTO parent, String path, int depth, SymbolDTO symbol, Direction direction) {
        NodeDTO result = find(structure, path);
        if (result == null) {
            if (parent != null && !parent.getExtensible()) {
                throw new IllegalStateException("Parent is not extensible: [" + parent + "]");
            }
            result = new NodeDTO();
            result.setStructure(structure);
            result.setParent(parent);
            result.setPath(path);
            result.setDepth(depth);
            result.setSymbol(symbol);
            result.setDirectionFlag(direction.toChar());
            result.setExtensible(false);
            logger.trace("Create node: {");
            logger.trace("  Structure: [" + result.getStructure() + "]");
            logger.trace("  Parent: [" + result.getParent() + "]");
            logger.trace("  Path: [" + result.getPath() + "]");
            logger.trace("  Depth: [" + result.getDepth() + "]");
            logger.trace("  Symbol: [" + result.getSymbol().toString(result.getDirection()) + "]");
            logger.trace("}");
            result = nodeRepository.save(result);
            structureDAO.updateMaxDepth(structure, result);
        }
        return result;
    }

    protected NodeDTO find(StructureDTO structure, String path) {
        return nodeRepository.getNodeByStructureAndPath(structure, path);
    }

    public boolean equals(NodeDTO node, Object other) {
        return equal(node, adapt(other, NodeDTO.class));
    }

    public int compareTo(NodeDTO node, NodeDTO other) {
        return node.compareTo(adapt(other, NodeDTO.class));
    }

    protected StringBuilder appendToPath(StringBuilder builder, SymbolDTO symbolDTO, Direction direction) {
        if (symbolDTO == null) {
            throw new IllegalArgumentException("Symbol should not be null");
        }
        if (builder.length() != 0) {
            builder.append(',');
        }
        builder.append(direction.toChar());
        builder.append(symbolDTO.getId());
        return builder;
    }

}
