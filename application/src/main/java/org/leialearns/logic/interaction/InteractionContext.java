package org.leialearns.logic.interaction;

import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.model.Version;
import org.leialearns.logic.structure.Structure;

/**
 * Defines the context of the exchange of information between <b>LEIA</b> and its environment.
 */
public interface InteractionContext extends Comparable<InteractionContext> {

    /**
     * Returns the identifying URI of this interaction context.
     * @return The identifying URI of this interaction context
     */
    String getURI();

    /**
     * Returns the collection of actions for this interaction context.
     * @return The collection of actions
     * @see org.leialearns.enumerations.Direction
     */
    Alphabet getActions();

    /**
     * Returns the collection of responses for this interaction context.
     * @return The collection of responses
     * @see org.leialearns.enumerations.Direction
     */
    Alphabet getResponses();

    /**
     * Returns the tree of interactions histories for this interaction context. This is the structure that
     * underlies the model of observed behavior and the model of expected behavior.
     * @return The model structure for this interaction context
     */
    Structure getStructure();

    /**
     * Returns an iterable of directed symbols that corresponds to the given strings. Each string in the given path
     * should start with a character that specifies the direction of the corresponding directed symbol. The
     * remainder of each string denotes a symbol in the corresponding alphabet. If the symbol does not yet exist in
     * the alphabet and the alphabet is not fixated yet,
     * then a new symbol is added (see {@link Alphabet#internalize(String)}).
     * @param path The representations of directed symbols to return
     * @return An iterable of directed symbols that corresponds to the given strings
     */
    DirectedSymbol.Iterable createPath(String... path);

    /**
     * Returns all versions of the given model type and with the given access mode. The versions are returned
     * in order of ascending ordinals.
     * @param minOrdinal The minimum for the ordinals of the returned versions
     * @param maxOrdinal The maximum for the ordinals of the returned versions
     * @param modelType The model type of the requested versions
     * @param accessMode The access model of the requested versions
     * @return All versions that match the parameters
     */
    Version.Iterable findVersionsInRange(long minOrdinal, long maxOrdinal, ModelType modelType, AccessMode accessMode);

    /**
     * Returns all versions for this interaction context.
     *
     * @return All versions for this interaction context
     */
    Version.Iterable getVersions();
}
