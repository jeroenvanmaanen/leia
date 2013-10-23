package org.leialearns.logic.structure;

import org.leialearns.logic.interaction.DirectedSymbol;
import org.leialearns.enumerations.Direction;
import org.leialearns.logic.interaction.Symbol;
import org.leialearns.utilities.TypedIterable;

/**
 * Defines an object that contains the interaction history tree.
 */
public interface Structure {

    /**
     * Returns the identifying URI of this structure.
     * @return The identifying URI of this structure
     * @see org.leialearns.logic.session.Root#createInteractionContext(String, String, String, String)
     */
    String getURI();

    /**
     * Finds or creates a node in the tree.
     * @param path The directed symbols that specify the path from the root of the tree to the requested node
     * @return The requested node
     */
    Node findOrCreateNode(DirectedSymbol.Iterable path);

    /**
     * Finds or creates a node in the tree.
     * @param action The last action
     * @param path The directed symbols that specify the path from node corresponding to the last action to the requested node
     * @return The requested node
     */
    Node findOrCreateNode(Symbol action, TypedIterable<DirectedSymbol> path);

    /**
     * Finds or creates a node in the tree.
     * @param symbol The symbol
     * @param direction The direction
     * @return The requested node
     */
    Node findOrCreateNode(Symbol symbol, Direction direction);

    /**
     * Returns an iterable that contains all nodes in the tree.
     * @return An iterable that contains all nodes in the tree
     */
    Node.Iterable findNodes();

    /**
     * Returns an iterable that contains all nodes in the tree with the given depth.
     * @param depth The depth of the requested nodes
     * @return An iterable that contains the requested nodes
     */
    Node.Iterable findNodes(int depth);

    /**
     * Returns an iterable that contains the root nodes of this structure.
     * @return An iterable that contains the root nodes of this structure
     */
    Node.Iterable findRootNodes();

    /**
     * Returns the maximum depth of all nodes in the tree.
     * @return The maximum depth of all nodes in the tree
     */
    int getMaxDepth();

    /**
     * Marks the given node as extensible.
     * @param node The node to mark as extensible
     */
    void markExtensible(Node node);

    /**
     * Logs all nodes in this structure.
     */
    void logNodes();

}
