package org.leialearns.graph.interaction;

import org.leialearns.graph.IdDaoSupport;
import org.leialearns.utilities.TypedIterable;

public class InteractionContextDAO extends IdDaoSupport<InteractionContextDTO> {

    public TypedIterable<DirectedSymbolDTO> createPath(InteractionContextDTO interactionContext, String... path) {
        return null; // TODO: implement
    }

    public int compareTo(InteractionContextDTO thisInteractionContext, Object that) {
        return thisInteractionContext.compareTo((InteractionContextDTO) adapt(that));
    }

}
