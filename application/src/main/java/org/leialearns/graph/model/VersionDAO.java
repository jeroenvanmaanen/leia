package org.leialearns.graph.model;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.interaction.InteractionContextDAO;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.graph.session.SessionDTO;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.Date;
import java.util.Set;

import static org.leialearns.utilities.Display.asDisplay;
import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.equal;

public class VersionDAO extends IdDaoSupport<VersionDTO> {
    private static final Logger logger = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());

    @Autowired
    private VersionRepository repository;

    @Autowired
    private ObservedDAO observedDAO;

    @Autowired
    private ToggledDAO toggledDAO;

    @Autowired
    private InteractionContextDAO interactionContextDAO;

    @Override
    protected VersionRepository getRepository() {
        return repository;
    }

    public VersionDTO createVersion(SessionDTO owner, ModelType modelType) {
        InteractionContextDTO interactionContext = owner.getInteractionContext();
        VersionDTO latestVersion = interactionContext.getLatestVersion();
        Long latestOrdinal = latestVersion == null ? null : latestVersion.getOrdinal();
        long nextOrdinal = latestOrdinal == null ? 1L : latestOrdinal + 1L;

        VersionDTO result = new VersionDTO();
        result.setInteractionContext(interactionContext);
        result.setModelType(modelType);
        result.setAccessMode(AccessMode.LOCKED);
        result.setOrdinal(nextOrdinal);
        result.setOwner(owner);
        result = save(result);

        interactionContext.setLatestVersion(result);
        interactionContextDAO.save(interactionContext);
        if (latestVersion != null) {
            latestVersion.setNextVersion(result);
        }

        logger.debug("Created version: " + result);
        return result;
    }

    @BridgeOverride
    public VersionDTO findVersion(SessionDTO owner, long ordinal) {
        InteractionContextDTO context = owner.getInteractionContext();
        logger.debug("Find version: {}.#{}", owner, ordinal);
        return repository.findByContextAndOrdinal(context, ordinal);
    }

    public VersionDTO findLastVersion(SessionDTO owner, ModelType modelType, AccessMode accessMode) {
        VersionDTO version = owner.getInteractionContext().getLatestVersion();
        logger.debug("Session: {}: {}", owner, showOwner(version));
        return findLastBeforeOrEqual(version, modelType, accessMode);
    }

    public VersionDTO findLastBefore(VersionDTO version, ModelType modelType, AccessMode accessMode) {
        VersionDTO previousVersion = repository.findPreviousVersion(version);
        logger.debug("Session: {}: {}", showOwner(version), showOwner(previousVersion));
        return findLastBeforeOrEqual(previousVersion, modelType, accessMode);
    }

    protected String showOwner(VersionDTO version) {
        return version == null ? "?" : display(version.getOwner());
    }

    protected VersionDTO findLastBeforeOrEqual(VersionDTO version, ModelType modelType, AccessMode accessMode) {
        logger.debug("Session: {}", (version == null ? null : version.getOwner()));
        while (version != null && version.getId() != null) {
            logger.debug("Version: {}", asDisplay(version));
            AccessMode versionAccessMode = version.getAccessMode();
            if ((modelType == null || version.getModelType() == modelType) && (accessMode == null ? versionAccessMode != AccessMode.EXCLUDE : versionAccessMode == accessMode)) {
                break;
            }
            version = repository.findPreviousVersion(version);
            logger.debug("Session: {}", showOwner(version));
        }
        if (version != null && version.getId() == null) {
            version = null;
        }
        return version;
    }

    public VersionDTO findRangeMax(VersionDTO lastVersion, VersionDTO previousVersion, ModelType modelType, AccessMode accessMode) {
        if (accessMode == null) {
            throw new IllegalArgumentException("The access mode should not be null");
        }
        SessionDTO owner = lastVersion.getOwner();
        logger.trace("Find range max: [" + previousVersion + "]: [" + lastVersion + "]: [" + modelType + "]: [" + accessMode + "]");
        logger.trace("Owner: [{}]", asDisplay(owner));

        long previousVersionOrdinal;
        if (previousVersion == null) {
            previousVersionOrdinal = -1;
        } else {
            previousVersionOrdinal = previousVersion.getOrdinal();
        }

        VersionDTO candidate;
        if (previousVersion == null) {
            InteractionContextDTO context = owner.getInteractionContext();
            logger.trace("Interaction context: [{}]", context);
            candidate = repository.findFirstVersion(context);
        } else {
            candidate = repository.findNextVersion(previousVersion);
        }
        logger.trace("Start at: {}", candidate);
        if (candidate != null) {
            long lastVersionOrdinal = lastVersion.getOrdinal();
            while (candidate.getOrdinal() < lastVersionOrdinal &&
                    (candidate.getModelType() != modelType || candidate.getAccessMode() == accessMode || candidate.getAccessMode() == AccessMode.EXCLUDE)) {
                VersionDTO newCandidate = repository.findNextVersion(candidate);
                logger.trace("New candidate: {}", newCandidate);
                if (newCandidate == null) {
                    break;
                }
                candidate = newCandidate;
            }

            logger.trace("Last candidate: {}", candidate);
            while (candidate != null && candidate.getOrdinal() > previousVersionOrdinal && (candidate.getModelType() != modelType || candidate.getAccessMode() != accessMode)) {
                candidate = repository.findPreviousVersion(candidate);
                logger.trace("Previous candidate: {}", candidate);
            }
            if (candidate != null && candidate.getOrdinal() <= previousVersionOrdinal) {
                candidate = null;
            }

            if (candidate != null) {
                candidate.setOwner(owner);
            }
        }
        logger.trace("Result: [" + candidate + "]");
        return candidate;
    }

    @BridgeOverride
    public TypedIterable<VersionDTO> findVersionsInRange(InteractionContextDTO interactionContext, long minOrdinal, long maxOrdinal, ModelType modelType, AccessMode accessMode) {
        logRange(interactionContext, minOrdinal, maxOrdinal);

        Set<VersionDTO> result;
        if (accessMode == null) {
            result = repository.findRange(interactionContext, minOrdinal, maxOrdinal, modelType.toChar());
        } else {
            result = repository.findRange(interactionContext, minOrdinal, maxOrdinal, modelType.toChar(), accessMode.toChar());
        }
        return new TypedIterable<>(result, VersionDTO.class);
    }

    protected void logRange(InteractionContextDTO context, long minOrdinal, long maxOrdinal) {
        if (logger.isTraceEnabled()) {
            logger.trace("Range: " + minOrdinal + " -> " + maxOrdinal + " {");
            Set<VersionDTO> versions = repository.findRange(context, minOrdinal, maxOrdinal);
            for (VersionDTO version : versions) {
                logger.trace("  " + version);
            }
            logger.trace("}");
        }
    }

    @BridgeOverride
    public VersionDTO findOrCreateLastVersion(SessionDTO owner, ModelType modelType, AccessMode accessMode) {
        VersionDTO result = findLastVersion(owner, modelType, accessMode);
        if (result == null) {
            result = createVersion(owner, modelType);
            if (accessMode != null) {
                result.setAccessMode(accessMode);
            }
            save(result);
        }
        return result;
    }

    public void setAccessMode(VersionDTO version, AccessMode accessMode, SessionDTO session) {
        if (session == null || !session.equals(version.getOwner())) {
            throw new IllegalArgumentException("This version does not belong to the given session: [" + version + "]: [" + session + "]");
        }
        version.setAccessMode(accessMode);
        save(version);
    }

    @Bean
    @Scope(value = "prototype")
    public CountedDTO createCountedVersion() {
        return new CountedDTO();
    }

    public CountedDTO createCountedVersion(VersionDTO version) {
        CountedDTO countedVersion = createCountedVersion();
        countedVersion.setVersion(version);
        return countedVersion;
    }

    public ObservedDTO createObservedVersion(VersionDTO version) {
        return observedDAO.findOrCreate(version);
    }

    public ToggledDTO findToggledVersion(VersionDTO version) {
        return toggledDAO.find(version);
    }

    @BridgeOverride
    public void waitForLock(VersionDTO versionDTO, SessionDTO session) throws InterruptedException {
        setAccessMode(versionDTO, AccessMode.LOCKING, session);
        long nextTime = new Date().getTime() + versionDTO.getLogInterval();
        while (true) {
            try {
                setAccessMode(versionDTO, AccessMode.LOCKED, session);
                break;
            } catch (IllegalStateException exception) {
                // Try again
            }
            Thread.sleep(5);
            long now = new Date().getTime();
            if (now > nextTime) {
                logger.warn("Waiting for lock on version: [" + this + "]: [" + session + "]");
                nextTime = now + versionDTO.getLogInterval();
            }
        }
    }

    public boolean equals(VersionDTO thisVersion, Object that) {
        return equal(thisVersion, adapt(that, VersionDTO.class));
    }

    public int compareTo(VersionDTO thisVersion, Object that) {
        return thisVersion.compareTo(adapt(that, VersionDTO.class));
    }

}
