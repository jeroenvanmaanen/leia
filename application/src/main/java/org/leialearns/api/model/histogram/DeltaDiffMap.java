package org.leialearns.api.model.histogram;

import org.leialearns.logic.structure.Node;

/**
 * Convenience interface to move around {@link DeltaDiff}s for a collection of {@link Node}s.
 */
public interface DeltaDiffMap extends java.util.Map<Node,DeltaDiff> {
}
