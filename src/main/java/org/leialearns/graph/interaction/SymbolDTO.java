package org.leialearns.graph.interaction;

import org.leialearns.bridge.FarObject;
import org.leialearns.enumerations.Direction;
import org.leialearns.graph.BaseGraphDTO;
import org.leialearns.logic.interaction.Symbol;

public class SymbolDTO extends BaseGraphDTO implements FarObject<Symbol> {

    public AlphabetDTO getAlphabet() {
        return null; // TODO: implement
    }

    public Long getOrdinal() {
        return null; // TODO: implement
    }

    public String getDenotation() {
        return null; // TODO: implement
    }

    public String toString(Direction direction) {
        return null; // TODO: implement
    }

    public String toShortString(Direction direction) {
        return null; // TODO: implement
    }

    public String toShortString() {
        return toShortString(null);
    }

    public DirectedSymbolDTO createDirectedSymbol(Direction direction) {
        return null; // TODO: implement
    }

    public int compareTo(SymbolDTO symbol) {
        return -1; // TODO: implement
    }

    @Override
    public Symbol declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
