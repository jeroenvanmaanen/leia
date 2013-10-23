package org.leialearns.logic.structure;

import static org.leialearns.utilities.Static.equal;

/**
 * Adds functionality to {@link Node}s.
 */
public class NodeHelper {

    /**
     * <em>See {@link Node#isPrefixOf(Node)}.</em>
     */
    public boolean isPrefixOf(Node node, Node other) {
        boolean result;
        if (other.getDepth() < node.getDepth()) {
            result = false;
        } else {
            Node otherAncestor = other;
            while (otherAncestor.getDepth() > node.getDepth()) {
                otherAncestor = otherAncestor.getParent();
            }
            result = equal(node,  otherAncestor);
        }
        return result;
    }

}
