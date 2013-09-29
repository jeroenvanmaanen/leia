package org.leialearns.graph.structure;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.enumerations.Direction;
import org.leialearns.graph.HasId;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.logic.structure.Node;
import java.io.Serializable;

public class NodeDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Node>, Comparable<NodeDTO> {

    public Long getId() {
        return null; // TODO: implement
    }

    public void setId(Long id) {
        // TODO: implement
    }

    public StructureDTO getStructure() {
        return null; // TODO: implement
    }

    public void setStructure(StructureDTO structure) {
        // TODO: implement
    }

    public NodeDTO getParent() {
        return null; // TODO: implement
    }

    public void setParent(NodeDTO parent) {
        // TODO: implement
    }

    public String getPath() {
        return null; // TODO: implement
    }

    public void setPath(String path) {
        // TODO: implement
    }

    public int getDepth() {
        return 0; // TODO: implement
    }

    public void setDepth(Integer depth) {
        // TODO: implement
    }

    public SymbolDTO getSymbol() {
        return null; // TODO: implement
    }

    public void setSymbol(SymbolDTO symbol) {
        // TODO: implement
    }

    public Direction getDirection() {
        return null; // TODO: implement
    }

    public void setDirection(Direction direction) {
        // TODO: implement
    }

    public void setDirectionFlag(char directionFlag) {
        // TODO: implement
    }

    public boolean getExtensible() {
        return false; // TODO: implement
    }

    public void setExtensible(boolean extensible) {
        // TODO: implement
    }

    public String toString() {
        return null; // TODO: implement
    }

    public void showPath(StringBuilder builder) {
        // TODO: implement
    }

    public void showPathReverse(StringBuilder builder) {
        // TODO: implement
    }

    public int compareTo(NodeDTO other) {
        return 0; // TODO: implement
    }

    public Node declareNearType() {
        return null; // TODO: implement
    }

}
