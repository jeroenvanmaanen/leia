package org.leialearns.graph.session;

import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.model.CountedDTO;
import org.leialearns.graph.model.ToggledDTO;
import org.leialearns.graph.model.VersionDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.utilities.Static.getLoggingClass;

public class SessionDAO extends IdDaoSupport<SessionDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    public SessionDAO(SessionRepository repository) {
        super(repository);
    }

    public SessionDTO create(RootDTO root, InteractionContextDTO interactionContext) {
        SessionDTO result = new SessionDTO();
        result.setRoot(root);
        result.setInteractionContext(interactionContext);
        result = save(result);
        logger.trace("Stack trace", new Throwable());
        return result;
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
        return sessionDTO.equals(adapt(other, SessionDTO.class));
    }

}
