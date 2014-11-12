package org.leialearns.graph.model;

import org.leialearns.api.model.histogram.Counter;
import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.DeclaresNearType;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.common.HasId;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.io.Serializable;

import static org.leialearns.graph.common.IdDaoSupport.toID;
import static org.leialearns.utilities.Display.displayParts;

@NodeEntity
public class CounterDTO extends BaseBridgeFacet implements HasId, Serializable, FarObject<Counter>, DeclaresNearType<Counter> {
    @GraphId
    private Long id;

    @RelatedTo(direction = Direction.OUTGOING, type = "IN_VERSION")
    private VersionDTO version;

    @RelatedTo(direction = Direction.OUTGOING, type = "FOR_NODE")
    private NodeDTO node;

    @RelatedTo(direction = Direction.OUTGOING, type = "FOR_SYMBOL")
    private SymbolDTO symbol;

    private long value;

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

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String toString() {
        Object showSymbol = symbol == null ? null : symbol.getDenotation();
        return displayParts("Counter", toID("C", version), node, showSymbol, value);
    }

    public Counter declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
