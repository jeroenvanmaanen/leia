package org.leialearns.logic.session;

import org.leialearns.logic.interaction.Alphabet;
import org.leialearns.logic.interaction.InteractionContext;
import org.leialearns.logic.model.Expectation;
import org.leialearns.logic.model.Fraction;
import org.leialearns.logic.model.Histogram;

/**
 * Defines the singleton that contains the objects that are at least partially persisted through the ORM framework,
 * but have no owner that is also persistent.
 */
public interface Root {

    /**
     * Creates a new interaction context instance. The URIs for the alphabets and the structure are derived from
     * the given URI.
     * @param interactionContextURI The URI that identifies the new interaction context
     * @return A new interaction context instance
     */
    InteractionContext createInteractionContext(String interactionContextURI);

    /**
     * Creates a new interaction context instance.
     * @param interactionContextURI The URI that identifies the new interaction context
     * @param actionsURI The URI that identifies the alphabet of actions
     * @param responsesURI The URI that identifies the alphabet of actions
     * @param structureURI The URI that identifies the tree of interaction histories
     * @return A new interaction context instance
     */
    InteractionContext createInteractionContext(String interactionContextURI, String actionsURI, String responsesURI, String structureURI);

    /**
     * Creates a session that can be used to create and/or obtain {@link org.leialearns.logic.model.Version}s of
     * various types of models.
     * @param interactionContextURI The URI that identifies the interaction context
     * @return A new session instance
     */
    Session createSession(String interactionContextURI);

    /**
     * Creates a session that can be used to create and/or obtain {@link org.leialearns.logic.model.Version}s of
     * various types of models.
     * @param interactionContext The interaction context for the new session
     * @return A new session instance
     */
    Session createSession(InteractionContext interactionContext);

    /**
     * Returns all alphabets that are available in the data store.
     * @return All alphabets that are available in the data store
     */
    Alphabet.Iterable findAlphabets();

    /**
     * Creates a new persistent fraction object.
     * @param index The index of the new fraction
     * @param numerator The numerator of the new object
     * @param denominator The denominator of the new object
     * @return The new fraction object
     */
    Fraction createFraction(long index, long numerator, long denominator);

    /**
     * Returns a persistent fraction object, creates a new one if necessary.
     * @param index The index of the fraction
     * @param numerator The numerator of the fraction
     * @param denominator The denominator of the fraction
     * @return The requested fraction object
     */
    Fraction findOrCreateFraction(long index, long numerator, long denominator);

    /**
     * Returns a persistent fraction object, creates a new one if necessary.
     * @param fraction The (probably transient) fraction to make persistent
     * @return The requested fraction object
     */
    Fraction findOrCreateFraction(Fraction fraction);

    /**
     * Creates a new persistent fraction object.
     * @param index The index of the new fraction
     * @param numerator The numerator of the new object
     * @param denominator The denominator of the new object
     * @param inOracle Indication of whether the new fraction is part of the oracle or not
     * @return The new fraction object
     */
    Fraction createFraction(long index, long numerator, long denominator, boolean inOracle);

    /**
     * Returns all fraction objects in the data store.
     * @return All fraction objects in the data store
     */
    Fraction.Iterable findFractions();

    /**
     * Returns the fraction object with the given index.
     * @param index The index of the requested fraction
     * @return The requested fraction
     */
    Fraction findFraction(long index);

    /**
     * Creates a new transient fraction object.
     * @param index The index of the new fraction
     * @param numerator The numerator of the new object
     * @param denominator The denominator of the new object
     * @return The new fraction object
     */
    Fraction createTransientFraction(long index, long numerator, long denominator);

    Expectation createExpectation();

}
