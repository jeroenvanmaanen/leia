package org.leialearns.api.interaction;

import org.leialearns.bridge.NearIterable;
import org.leialearns.enumerations.Direction;

/**
 * Defines a symbol with the additional direction property.
 */
public interface DirectedSymbol {

    /**
     * Returns the direction of this directed symbol.
     * @return The direction of this directed symbol
     */
    Direction getDirection();

    /**
     * Returns the (undirected) symbol that is behind this directed symbol.
     * @return The undirected symbol
     */
    Symbol getSymbol();

    /**
     * Represents a <code>NearIterable</code> that returns directed symbol items.
     */
    interface Iterable extends NearIterable<DirectedSymbol> {
        DirectedSymbol declareNearType();
    }
}
