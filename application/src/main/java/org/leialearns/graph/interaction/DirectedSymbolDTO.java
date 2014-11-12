package org.leialearns.graph.interaction;

import org.leialearns.bridge.FarObject;
import org.leialearns.enumerations.Direction;
import org.leialearns.api.interaction.DirectedSymbol;

public class DirectedSymbolDTO implements FarObject<DirectedSymbol> {
    private Direction direction;
    private SymbolDTO symbol;

    public DirectedSymbolDTO(Direction direction, SymbolDTO symbol) {
        this.direction = direction;
        this.symbol = symbol;
    }

    public SymbolDTO getSymbol() {
        return symbol;
    }

    public Direction getDirection() {
        return direction;
    }

    public String toString() {
        return symbol.toString(direction);
    }

    @Override
    public DirectedSymbol declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
