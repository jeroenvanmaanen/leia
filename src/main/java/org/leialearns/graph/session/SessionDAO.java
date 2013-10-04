package org.leialearns.graph.session;

import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.model.CountedDTO;
import org.leialearns.graph.model.ToggledDTO;
import org.leialearns.graph.model.VersionDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.utilities.TypedIterable;

public class SessionDAO extends IdDaoSupport<SessionDTO> {

    public SessionDTO create(RootDTO root, InteractionContextDTO interactionContext) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void logVersions(SessionDTO owner, String label, TypedIterable<VersionDTO> versions) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public CountedDTO createCountedVersion(SessionDTO owner) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public ToggledDTO createToggledVersion(SessionDTO owner, NodeDTO node, boolean include) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public SessionDTO refresh(final SessionDTO session) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void flush(SessionDTO session) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public boolean equals(SessionDTO sessionDTO, Object other) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

}
