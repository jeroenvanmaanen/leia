package org.leialearns.graph.interaction;

import org.leialearns.bridge.FarObject;
import org.leialearns.enumerations.Direction;
import org.leialearns.logic.interaction.DirectedSymbol;

public class DirectedSymbolDTO implements FarObject<DirectedSymbol> {

    public SymbolDTO getSymbol() {
        return null; // TODO: implement
    }

    public Direction getDirection() {
        return null; // TODO: implement
    }

    @Override
    public DirectedSymbol declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
