package org.leialearns.graph.structure;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.enumerations.Direction;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.utilities.BaseExpression;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.leialearns.utilities.Display.asDisplay;
import static org.leialearns.utilities.Static.equal;
import static org.leialearns.utilities.Static.getLoggingClass;

public class NodeDAO extends IdDaoSupport<NodeDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private StructureDAO structureDAO;

    public TypedIterable<NodeDTO> findNodes(StructureDTO structure) {
        return new TypedIterable<>(nodeRepository.findNodesByStructure(structure), NodeDTO.class);
    }

    @BridgeOverride
    public TypedIterable<NodeDTO> findRootNodes(StructureDTO structure) {
        return new TypedIterable<>(nodeRepository.findRootNodes(structure), NodeDTO.class);
    }

    @BridgeOverride
    public TypedIterable<NodeDTO> findChildren(NodeDTO node) {
        return new TypedIterable<>(nodeRepository.findChildren(node), NodeDTO.class);
    }

    public NodeDTO find(StructureDTO structure, TypedIterable<DirectedSymbolDTO> path) {
        StringBuilder builder = new StringBuilder();
        for (DirectedSymbolDTO directedSymbol : path) {
            appendToPath(builder, directedSymbol.getSymbol(), directedSymbol.getDirection());
        }
        return find(structure, builder.toString());
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

    protected NodeDTO findOrCreate(final StructureDTO structure, NodeDTO parent, String path, int depth, SymbolDTO symbol, Direction direction) {
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
            if (parent == null) {
                linkTo(structure, "HAS_ROOT", result);
                logger.debug("Root nodes: {}: {}", asDisplay(structure), new BaseExpression<Set<NodeDTO>>() {
                    public Set<NodeDTO> get() {
                        return nodeRepository.findRootNodes(structure);
                    }
                });
            }
            logger.debug("Node structure after save: [{}]", result.getStructure());
            structureDAO.updateMaxDepth(structure, result);
        }
        return result;
    }

    protected NodeDTO find(StructureDTO structure, String path) {
        return nodeRepository.getNodeByStructureAndPath(structure, path);
    }

    @BridgeOverride
    public void markExtensible(StructureDTO structure, NodeDTO node) {
        if (!structure.equals(node.getStructure())) {
            throw new IllegalArgumentException("Node does not belong to this structure: [" + node + "]: [" + structure + "]");
        }
        node.setExtensible(true);
        logger.trace("Marked extensible: {}", node);
        nodeRepository.save(node);
    }

    public boolean equals(NodeDTO node, Object other) {
        return equal(node, adapt(other, NodeDTO.class));
    }

    public int compareTo(NodeDTO node, NodeDTO other) {
        return node.compareTo(adapt(other, NodeDTO.class));
    }

    public String toString(NodeDTO node) {
        if (node.getStructure() == null) {
            node = nodeRepository.findOne(node.getId());
        }
        StringBuilder builder = new StringBuilder("[Node|");
        builder.append(toID(null, node));
        builder.append('|');
        builder.append(toID("S", node.getStructure()));
        builder.append('|');
        builder.append(node.getExtensible() ? "E" : "T");
        builder.append("|(");
        showPathReverse(node, builder);
        builder.append(")]");
        return builder.toString();
    }

    public void showPath(NodeDTO node, StringBuilder builder) {
        if (node.getStructure() == null) {
            node = nodeRepository.findOne(node.getId());
        }
        NodeDTO parent = node.getParent();
        if (parent != null) {
            showPath(parent, builder);
            builder.append(' ');
        }
        SymbolDTO symbol = node.getSymbol();
        builder.append(symbol == null ? "?" : symbol.toShortString(node.getDirection()));
    }

    public void showPathReverse(NodeDTO node, StringBuilder builder) {
        if (node.getStructure() == null) {
            node = nodeRepository.findOne(node.getId());
        }
        NodeDTO parent = node.getParent();
        SymbolDTO symbol = node.getSymbol();
        builder.append(symbol == null ? "?" : symbol.toShortString(node.getDirection()));
        if (parent != null) {
            builder.append(' ');
            showPathReverse(parent, builder);
        }
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
