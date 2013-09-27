package org.leialearns.graph.model;

import org.leialearns.bridge.DeclaresNearType;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.BaseGraphDTO;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.logic.model.Counter;
import java.io.Serializable;

public class CounterDTO extends BaseGraphDTO implements Serializable, FarObject<Counter>, DeclaresNearType<Counter> {

    public Long getId() {
        return null; // TODO: implement
    }

    public void setId(Long id) {
        // TODO: implement
    }

    public VersionDTO getVersion() {
        return null; // TODO: implement
    }

    public void setVersion(VersionDTO version) {
        // TODO: implement
    }

    public NodeDTO getNode() {
        return null; // TODO: implement
    }

    public void setNode(NodeDTO node) {
        // TODO: implement
    }

    public SymbolDTO getSymbol() {
        return null; // TODO: implement
    }

    public void setSymbol(SymbolDTO symbol) {
        // TODO: implement
    }

    public long getValue() {
        return 0; // TODO: implement
    }

    public void setValue(long value) {
        // TODO: implement
    }

    public String toString() {
        return null; // TODO: implement
    }

    public Counter declareNearType() {
        return null; // TODO: implement
    }

}
