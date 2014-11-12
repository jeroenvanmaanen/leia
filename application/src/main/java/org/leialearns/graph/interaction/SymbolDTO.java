package org.leialearns.graph.interaction;

import org.leialearns.api.enumerations.Direction;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;

import static org.leialearns.utilities.Display.displayParts;
import static org.leialearns.utilities.Display.show;
import static org.leialearns.utilities.L.literal;
import static org.leialearns.utilities.Static.equal;
import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;

@NodeEntity
public class SymbolDTO extends BaseBridgeFacet implements HasId, FarObject<Symbol>, Comparable<SymbolDTO> {
    @GraphId
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @RelatedTo(direction = INCOMING, type = "HAS_WORD")
    private AlphabetDTO alphabet;

    @RelatedTo(direction = OUTGOING, type = "NEXT_SYMBOL")
    private SymbolDTO nextSymbol;

    @Indexed(unique = false)
    private String denotation;

    private Long ordinal;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public AlphabetDTO getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(AlphabetDTO alphabet) {
        this.alphabet = alphabet;
    }

    @SuppressWarnings("unused")
    public SymbolDTO getNextSymbol() {
        return nextSymbol;
    }

    public void setNextSymbol(SymbolDTO nextSymbol) {
        this.nextSymbol = nextSymbol;
    }

    public String getDenotation() {
        return denotation;
    }

    public void setDenotation(String denotation) {
        this.denotation = denotation;
    }

    public Long getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Long ordinal) {
        if (this.ordinal != null) {
            throw new IllegalArgumentException("Ordinal is already set to: " + this.ordinal + ": (" + ordinal + ")");
        }
        this.ordinal = ordinal;
    }

    @Override
    public String toString() {
        return toString(null);
    }

    public String toString(Direction direction) {
        return displayParts("Symbol", id, ordinal, toShortString(direction));
    }

    public Object toShortString(Direction direction) {
        char directionChar = (direction == null ? '?' : direction.toChar());
        return literal(directionChar + show(denotation));
    }

    @BridgeOverride
    public Object toShortString() {
        return toShortString(null);
    }

    public DirectedSymbolDTO createDirectedSymbol(Direction direction) {
        return new DirectedSymbolDTO(direction, this);
    }

    @Override
    public int compareTo(@NotNull SymbolDTO symbol) {
        int result = this.alphabet.compareTo(symbol.getAlphabet());
        if (result == 0) {
            result = this.denotation.compareTo(symbol.getDenotation());
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        boolean result;
        if (other instanceof SymbolDTO) {
            SymbolDTO otherSymbol = (SymbolDTO) other;
            result = equal(alphabet, otherSymbol.getAlphabet()) && equal(denotation, otherSymbol.getDenotation());
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return (alphabet == null ? 0 : alphabet.hashCode()) + (denotation == null ? 0 : denotation.hashCode());
    }

    @Override
    public Symbol declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
