package org.leialearns.graph.model;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.DeclaresNearType;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.api.model.expectation.Estimate;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.io.Serializable;

import static org.leialearns.graph.IdDaoSupport.toID;
import static org.leialearns.utilities.Display.displayParts;

@NodeEntity
public class EstimateDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Estimate>, DeclaresNearType<Estimate> {
    @GraphId
    private Long id;

    @RelatedTo(direction = Direction.OUTGOING, type = "FOR_VERSION")
    private VersionDTO version;

    @RelatedTo(direction = Direction.OUTGOING, type = "FOR_NODE")
    private NodeDTO node;

    @RelatedTo(direction = Direction.OUTGOING, type = "FOR_SYMBOL")
    private SymbolDTO symbol;

    @RelatedTo(direction = Direction.OUTGOING, type = "HAS_FRACTION")
    private FractionEstimateDTO fraction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VersionDTO getVersion() {
        return version;
    }

    public void setVersion(VersionDTO version) {
        this.version = version;
    }

    public NodeDTO getNode() {
        return node;
    }

    public void setNode(NodeDTO node) {
        this.node = node;
    }

    public SymbolDTO getSymbol() {
        return symbol;
    }

    public void setSymbol(SymbolDTO symbol) {
        this.symbol = symbol;
    }

    public FractionEstimateDTO getFraction() {
        return fraction;
    }

    public void setFraction(FractionEstimateDTO fraction) {
        this.fraction = fraction;
    }

    public String toString() {
        Object showSymbol = symbol == null ? null : symbol.getDenotation();
        return displayParts("Estimate", toID("F", version), node, showSymbol, fraction);
    }

    public Estimate declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
