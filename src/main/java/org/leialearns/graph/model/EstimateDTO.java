package org.leialearns.graph.model;

import org.leialearns.bridge.DeclaresNearType;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.BaseGraphDTO;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.logic.model.Estimate;
import java.io.Serializable;

public class EstimateDTO extends BaseGraphDTO implements Serializable, FarObject<Estimate>, DeclaresNearType<Estimate> {

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

    public FractionDTO getFraction() {
        return null; // TODO: implement
    }

    public void setFraction(FractionDTO fraction) {
        // TODO: implement
    }

    public String toString() {
        return null; // TODO: implement
    }

    public Estimate declareNearType() {
        return null; // TODO: implement
    }

}
