package org.leialearns.graph.structure;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.enumerations.Direction;
import org.leialearns.graph.HasId;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.logic.structure.Node;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.io.Serializable;

import static org.leialearns.graph.IdDaoSupport.toID;
import static org.leialearns.utilities.Static.equal;
import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;

@NodeEntity
public class NodeDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Node>, Comparable<NodeDTO> {
    @GraphId
    private Long id;

    @RelatedTo(direction = INCOMING, type = "HAS_NODE")
    private StructureDTO structure;

    @RelatedTo(direction = INCOMING, type = "HAS_CHILD")
    private NodeDTO parent;

    @RelatedTo(direction = OUTGOING, type = "FOR_SYMBOL")
    private SymbolDTO symbol;

    @Indexed(indexName = "nodePath", unique = false, level = Indexed.Level.GLOBAL)
    private String path;

    private Integer depth;
    private Character directionFlag;
    private Boolean extensible;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StructureDTO getStructure() {
        return structure;
    }

    public void setStructure(StructureDTO structure) {
        this.structure = structure;
    }

    public NodeDTO getParent() {
        return parent;
    }

    public void setParent(NodeDTO parent) {
        this.parent = parent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public SymbolDTO getSymbol() {
        return symbol;
    }

    public void setSymbol(SymbolDTO symbol) {
        this.symbol = symbol;
    }

    public Direction getDirection() {
        return Direction.valueOf(directionFlag);
    }

    public void setDirection(Direction direction) {
        directionFlag = direction.toChar();
    }

    public void setDirectionFlag(char directionFlag) {
        Direction direction = Direction.valueOf(directionFlag);
        if (direction == null) {
            throw new IllegalArgumentException("Not a valid direction flag: [" + directionFlag + "]");
        }
        this.directionFlag = directionFlag;
    }

    public boolean getExtensible() {
        return Boolean.TRUE.equals(extensible);
    }

    public void setExtensible(boolean extensible) {
        this.extensible = extensible;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("[Node|" + toID(null, this) + "|" + (getExtensible() ? "E" : "T") + "|(");
        showPathReverse(builder);
        builder.append(")]");
        return builder.toString();
    }

    public void showPath(StringBuilder builder) {
        if (parent != null) {
            parent.showPath(builder);
            builder.append(' ');
        }
        builder.append(symbol == null ? "?" : symbol.toShortString(getDirection()));
    }

    public void showPathReverse(StringBuilder builder) {
        builder.append(symbol == null ? "?" : symbol.toShortString(getDirection()));
        if (parent != null) {
            builder.append(' ');
            parent.showPathReverse(builder);
        }
    }

    public int compareTo(NodeDTO other) {
        int result = Long.signum(structure.getId() - other.getStructure().getId());
        return (result == 0 ? Long.signum(id - other.getId()) : result);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof NodeDTO && equal(id, ((NodeDTO) other).getId());
    }

    @Override
    public int hashCode() {
        return id == null ? -1 : (int) ((long) id);
    }

    public Node declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
