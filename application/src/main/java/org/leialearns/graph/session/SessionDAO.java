package org.leialearns.graph.session;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.model.CountedDTO;
import org.leialearns.graph.model.ToggledDAO;
import org.leialearns.graph.model.ToggledDTO;
import org.leialearns.graph.model.VersionDAO;
import org.leialearns.graph.model.VersionDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Display.displayWithTypes;
import static org.leialearns.utilities.Static.getLoggingClass;

public class SessionDAO extends IdDaoSupport<SessionDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private VersionDAO versionDAO;

    @Autowired
    private ToggledDAO toggledDAO;

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
    public CountedDTO createCountedVersion(SessionDTO owner) {
        VersionDTO version = versionDAO.createVersion(owner, ModelType.COUNTED);
        CountedDTO result = versionDAO.createCountedVersion();
        result.setVersion(version);
        return result;
    }

    @BridgeOverride
    public ToggledDTO createToggledVersion(SessionDTO owner, NodeDTO node, boolean include) {
        return toggledDAO.create(owner, node, include);
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