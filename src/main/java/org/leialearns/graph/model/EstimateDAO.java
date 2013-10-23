package org.leialearns.graph.model;

import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.utilities.TypedIterable;

public class EstimateDAO extends IdDaoSupport<EstimateDTO> {

    public EstimateDTO findEstimate(VersionDTO version, NodeDTO node, SymbolDTO symbol) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<EstimateDTO> findEstimates(VersionDTO version, NodeDTO node) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<EstimateDTO> findEstimates(VersionDTO version) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public EstimateDTO createEstimate(ExpectedDTO expected, NodeDTO node, SymbolDTO symbol, FractionBaseDTO fraction) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public EstimateDTO createEstimate(ToggledDTO toggled, NodeDTO node, SymbolDTO symbol, FractionBaseDTO fraction) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public EstimateDTO findOrCreate(VersionDTO version, NodeDTO node, SymbolDTO symbol, FractionBaseDTO fraction) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public EstimateDTO create(VersionDTO version, NodeDTO node, SymbolDTO symbol, FractionBaseDTO fraction) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void copyEstimates(final ExpectedDTO expected, final VersionDTO sourceVersion) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

}
