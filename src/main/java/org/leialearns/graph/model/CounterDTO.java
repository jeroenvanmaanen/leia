package org.leialearns.graph.model;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.DeclaresNearType;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.logic.model.Counter;
import java.io.Serializable;

public class CounterDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Counter>, DeclaresNearType<Counter> {

    public Long getId() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setId(Long id) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public VersionDTO getVersion() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setVersion(VersionDTO version) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public NodeDTO getNode() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setNode(NodeDTO node) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public SymbolDTO getSymbol() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setSymbol(SymbolDTO symbol) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public long getValue() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setValue(long value) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public String toString() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public Counter declareNearType() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

}
