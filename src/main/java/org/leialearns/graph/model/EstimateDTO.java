package org.leialearns.graph.model;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.DeclaresNearType;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.logic.model.Estimate;
import java.io.Serializable;

public class EstimateDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Estimate>, DeclaresNearType<Estimate> {

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

    public FractionBaseDTO getFraction() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setFraction(FractionBaseDTO fraction) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public String toString() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public Estimate declareNearType() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

}
