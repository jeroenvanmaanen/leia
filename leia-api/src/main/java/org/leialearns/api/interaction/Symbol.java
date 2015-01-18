package org.leialearns.api.interaction;

import org.leialearns.api.common.HasDescriptionLength;
import org.leialearns.api.enumerations.Direction;
import org.leialearns.bridge.NearIterable;

/**
 * <p>Defines the unit of communication.</p>
 *
 * <p>A symbol is a fixed object: its properties do not change.</p>
 *
 * @see Alphabet
 */
public interface Symbol extends Comparable<Symbol>, HasDescriptionLength {

    /**
     * Returns the alphabet that contains this symbol.
     * @return The alphabet that contains this symbol
     */
    Alphabet getAlphabet();

    /**
     * Returns the ordinal number of this symbol within the containing alphabet. Typically ordinal numbers are
     * assigned in order of the moment that the symbols are added to their alphabet.
     * @return The ordinal number of this symbol within the containing alphabet
     */
    long getOrdinal();

    /**
     * Returns the string that denotes this symbol.
     * @return The string that denotes this symbol
     */
    String getDenotation();

    /**
     * Returns the string that denotes this symbol together with (normally preceded by) the denotation of the
     * given direction.
     * @param direction The direction to show with this symbol
     * @return The denotation of direction and symbol
     */
    String toString(Direction direction);

    /**
     * Returns a short string that denotes this symbol.
     * @return A short string that denotes this symbol
     */
    @SuppressWarnings("unused")
    String toShortString();

    /**
     * Returns a short string that denotes this symbol together with (normally preceded by) the denotation of the
     * given direction.
     * @param direction The direction to show with this symbol
     * @return A short string that shows the direction and this symbol
     */
    @SuppressWarnings("unused")
    String toShortString(Direction direction);

    /**
     * Creates a directed symbol that corresponds to the given direction and this symbol.
     * @param direction The direction to use
     * @return The directed symbol
     */
    DirectedSymbol createDirectedSymbol(Direction direction);

    /**
     * Represents a <code>NearIterable</code> that returns symbol items.
     */
    interface Iterable extends NearIterable<Symbol> {
        Symbol declareNearType();
    }
}
