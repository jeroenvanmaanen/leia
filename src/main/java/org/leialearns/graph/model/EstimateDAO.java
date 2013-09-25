package org.leialearns.graph.model;

import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.utilities.TypedIterable;

public class EstimateDAO extends IdDaoSupport<EstimateDTO> {

    public EstimateDTO findEstimate(VersionDTO version, NodeDTO node, SymbolDTO symbol) {
        return null; // TODO: implement
    }

    public TypedIterable<EstimateDTO> findEstimates(VersionDTO version, NodeDTO node) {
        return null; // TODO: implement
    }

    public TypedIterable<EstimateDTO> findEstimates(VersionDTO version) {
        return null; // TODO: implement
    }

    public EstimateDTO createEstimate(ExpectedDTO expected, NodeDTO node, SymbolDTO symbol, FractionDTO fraction) {
        return null; // TODO: implement
    }

    public EstimateDTO createEstimate(ToggledDTO toggled, NodeDTO node, SymbolDTO symbol, FractionDTO fraction) {
        return null; // TODO: implement
    }

    public EstimateDTO findOrCreate(VersionDTO version, NodeDTO node, SymbolDTO symbol, FractionDTO fraction) {
        return null; // TODO: implement
    }

    public EstimateDTO create(VersionDTO version, NodeDTO node, SymbolDTO symbol, FractionDTO fraction) {
        return null; // TODO: implement
    }

    public void copyEstimates(final ExpectedDTO expected, final VersionDTO sourceVersion) {
        // TODO: implement
    }

}
