package org.leialearns.logic.interaction;

import org.leialearns.bridge.NearIterable;

/**
 * <p>Defines a collection of {@link Symbol}s that can be used together.</p>
 *
 * <p>The description length of symbols in an alphabet depends on whether the alphabet is finite and complete,
 * or whether it is potentially infinite and still possible to add new symbols to it. The actual state
 * of the alphabet in this respect can be queried using the
 * method {@link #isFixated()}. When an alphabet is created, it starts out
 * as potentially infinite and <code>isFixated</code> returns <code>false</code>. When all symbols are added to the
 * alphabet, it can be marked as finite and complete by calling {@link #fixate()}.
 * To ensure that the properties of {@link Symbol}s (in particular the description length property) never change
 * however, calling the method <code>isFixated()</code> on the same alphabet should always return the same value.
 * So it is  best to add all symbols first, and then mark the alphabet as fixated, before calling
 * <code>isFixated</code> or querying the description length of any of its symbols.</p>
 */
public interface Alphabet extends Comparable<Alphabet> {

    /**
     * Returns the identifying URI of this alphabet.
     * @return The identifying URI of this alphabet
     * @see org.leialearns.logic.session.Root#createInteractionContext(String, String, String, String)
     */
    String getURI();

    /**
     * Marks this alphabet as finite and complete.
     */
    void fixate();

    /**
     * Returns a flag that indicates whether this alphabet should be treated as finite and complete. This method
     * will always return the same value for the same object, and may not reflect a call to {@link #fixate()} if
     * this object had already committed to a potentially infinite state.
     * @return <code>true</code> if the alphabet should be treated as finite and complete; <code>false</code> otherwise
     */
    boolean isFixated();

    /**
     * Adds a symbol to the alphabet.
     * @param symbol The string denotation of the symbol
     * @return The new or existing symbol object that corresponds to the given denotation
     */
    Symbol internalize(String symbol);

    /**
     * Returns the largest ordinal of all symbols in this alphabet.
     * @return The largest ordinal of all symbols in this alphabet
     */
    Long findLargestSymbolOrdinal();

    /**
     * Returns the uniform description length for all symbols in this alphabet.
     * @return The uniform description length for all symbols in this alphabet
     * @throws java.lang.IllegalStateException If this alphabet is not fixated
     */
    long getFixatedDescriptionLength();

    /**
     * Represents a <code>NearIterable</code> that returns alphabet items.
     */
    interface Iterable extends NearIterable<Alphabet> {
        Alphabet declareNearType();
    }
}
