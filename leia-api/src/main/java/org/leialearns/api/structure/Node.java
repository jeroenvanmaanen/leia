package org.leialearns.api.structure;

import org.leialearns.api.enumerations.Direction;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.bridge.NearIterable;

import javax.validation.constraints.NotNull;

/**
 * Defines the objects that represent nodes in the interaction history tree.
 */
public interface Node extends Comparable<Node> {

    /**
     * Returns the structure that contains this node.
     * @return The structure that contains this node
     */
    Structure getStructure();

    /**
     * Returns the parent node of this node. In an interaction history tree, the root of the tree represents the
     * present. The nodes of the first level represent the last action of the <b>LEIA</b> system and the
     * farther the nodes are from the root of the tree, the farther in the past the corresponding symbols are to be
     * found in the current state of the encounter. (See org.leialearns.command.encounter.Encounter)
     * @return The parent node of this node
     */
    Node getParent();

    /**
     * Returns an iterable that contains the child nodes of this node.
     * @return An iterable that contains the child nodes of this node
     */
    Node.Iterable findChildren();

    /**
     * Returns the symbol associated with this node.
     * @return The symbol associated with this node
     */
    Symbol getSymbol();

    /**
     * Returns the direction associated with this node.
     * @return The direction associated with this node
     */
    Direction getDirection();

    /**
     * Returns the depth of the node in the tree.
     * @return The depth of the node in the tree
     */
    int getDepth();

    /**
     * Returns a flag that indicates whether the tree may be extended beyond this node.
     * @return <code>true</code> if the tree may be extended beyond this node; <code>false</code> otherwise
     */
    boolean getExtensible();

    /**
     * Finds or creates a child node of this node with the given symbol and direction.
     * @param symbol The symbol associated with the requested node
     * @param direction The direction associated with the requested node
     * @return The requested child node
     * @throws IllegalStateException if this node is not extensible
     */
    Node findOrCreate(Symbol symbol, Direction direction);

    /**
     * Returns a flag that indicates whether this node is a prefix of the given node
     * @param other The node to compare against
     * @return <code>true</code> if this node is a prefix of the other node; <code>false</code> otherwise
     */
    boolean isPrefixOf(Node other);

    /**
     * Appends a representation of the path to this node to the given string builder. The root node is
     * located at the front of the representation.
     * @param builder The builder to use
     */
    void showPath(StringBuilder builder);

    /**
     * Appends a representation of the path to this node to the given string builder. The root node is
     * located at the end of the representation. This order is often the most intuitive, because it lists
     * symbols from past to present instead of the other way around.
     * @param builder The builder to use
     */
    void showPathReverse(StringBuilder builder);

    int compareTo(@NotNull Node other);

    /**
     * Represents a <code>NearIterable</code> that returns node items.
     */
    interface Iterable extends NearIterable<Node> {
        Node declareNearType();
    }

}
