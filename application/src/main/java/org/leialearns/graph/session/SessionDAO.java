package org.leialearns.graph.session;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.common.TypedIterable;
import org.leialearns.graph.common.IdDaoSupport;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.graph.model.VersionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.common.Display.display;
import static org.leialearns.common.Display.displayWithTypes;

public class SessionDAO extends IdDaoSupport<SessionDTO> {
    private static final Logger logger = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());

    @Autowired
    private SessionRepository repository;

    @Override
    protected SessionRepository getRepository() {
        return repository;
    }

    public SessionDTO create(RootDTO root, InteractionContextDTO interactionContext) {
        logger.debug("Context: {}", interactionContext);
        SessionDTO result = new SessionDTO();
        result.setRoot(root);
        result.setInteractionContext(interactionContext);
        result = save(result);
        InteractionContextDTO resultContext = result.getInteractionContext();
        logger.debug("Context: {}, {}", resultContext, resultContext.getURI());
        logger.trace("Stack trace", new Throwable());
        return result;
    }

    @BridgeOverride
    public void logVersions(SessionDTO owner, String label, TypedIterable<VersionDTO> versions) {
        if (logger.isDebugEnabled()) {
            String prefix = (label == null || label.isEmpty() ? "" : label + ": ");
            logger.debug("Versions: " + prefix + "Iterable class: " + displayWithTypes(versions.getClass()));
            logger.debug("Versions: " + prefix + "{");
            for (VersionDTO version : versions) {
                logger.debug("  " + display(version));
            }
            logger.debug("}");
        }
    }

    @BridgeOverride
    public SessionDTO refresh(final SessionDTO session) {
        logger.trace("Refresh called: Ignored.");
        return session;
    }

    @BridgeOverride
    public void flush(SessionDTO session) {
        logger.trace("Flush called: Ignored.");
    }

    public boolean equals(SessionDTO sessionDTO, Object other) {
        return sessionDTO.equals(adapt(other, SessionDTO.class));
    }

}
