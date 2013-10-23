package org.leialearns.graph.model;

import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.graph.interaction.InteractionContextRepository;
import org.leialearns.graph.session.SessionDTO;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.Date;

import static org.leialearns.utilities.Display.asDisplay;
import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.equal;
import static org.leialearns.utilities.Static.getLoggingClass;

public class VersionDAO extends IdDaoSupport<VersionDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final VersionRepository versionRepository;

    @Autowired
    private ObservedDAO observedDAO;

    @Autowired
    private InteractionContextRepository interactionContextRepository;

    @Autowired
    public VersionDAO(VersionRepository repository) {
        super(repository);
        versionRepository = repository;
    }

    public VersionDTO createVersion(SessionDTO owner, ModelType modelType) {
        InteractionContextDTO interactionContext = owner.getInteractionContext();
        VersionDTO result = new VersionDTO();
        result.setInteractionContext(interactionContext);
        result.setModelType(modelType);
        result.setAccessMode(AccessMode.LOCKED);
        result = save(result);
        result.setOwner(owner);
        setOrdinal(result);
        logger.debug("Created version: " + result);
        return result;
    }

    public VersionDTO findVersion(SessionDTO owner, long ordinal) {
        InteractionContextDTO context = owner.getInteractionContext();
        logger.debug("Find version: {}.#{}", owner, ordinal);
        return versionRepository.findByContextAndOrdinal(context, ordinal);
    }

    public VersionDTO findExpected(CountedDTO counted) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public VersionDTO findLastVersion(SessionDTO owner, ModelType modelType, AccessMode accessMode) {
        VersionDTO version = versionRepository.findLastVersion(owner.getInteractionContext());
        logger.debug("Session: {}: {}", owner, showOwner(version));
        return findLastBeforeOrEqual(version, modelType, accessMode);
    }

    public VersionDTO findLastBefore(VersionDTO version, ModelType modelType, AccessMode accessMode) {
        VersionDTO previousVersion = versionRepository.findPreviousVersion(version);
        logger.debug("Session: {}: {}", showOwner(version), showOwner(previousVersion));
        return findLastBeforeOrEqual(previousVersion, modelType, accessMode);
    }

    protected String showOwner(VersionDTO version) {
        return version == null ? "?" : display(version.getOwner());
    }

    protected VersionDTO findLastBeforeOrEqual(VersionDTO version, ModelType modelType, AccessMode accessMode) {
        logger.debug("Session: {}", (version == null ? null : version.getOwner()));
        while (version != null && version.getId() != null) {
            AccessMode versionAccessMode = version.getAccessMode();
            if ((modelType == null || version.getModelType() == modelType) && (accessMode == null ? versionAccessMode != AccessMode.EXCLUDE : versionAccessMode == accessMode)) {
                break;
            }
            version = versionRepository.findPreviousVersion(version);
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

        VersionDTO result;
        if (previousVersion == null) {
            InteractionContextDTO context = owner.getInteractionContext();
            logger.trace("Interaction context: [{}]", context);
            result = versionRepository.findFirstVersion(context);
        } else {
            result = versionRepository.findNextVersion(previousVersion);
        }
        if (result != null) {
            long lastVersionOrdinal = lastVersion.getOrdinal();
            while (result.getOrdinal() < lastVersionOrdinal &&
                    (result.getModelType() != modelType || result.getAccessMode() == accessMode || result.getAccessMode() == AccessMode.EXCLUDE)) {
                VersionDTO newResult = versionRepository.findNextVersion(result);
                if (newResult == null) {
                    break;
                }
                result = newResult;
            }

            while (result.getOrdinal() > previousVersionOrdinal && result.getModelType() != modelType && result.getAccessMode() != accessMode) {
                result = versionRepository.findPreviousVersion(result);
            }
            if (result != null && result.getOrdinal() <= previousVersionOrdinal) {
                result = null;
            }

            if (result != null) {
                result.setOwner(owner);
            }
        }
        logger.trace("Result: [" + result + "]");
        return result;
    }

    public TypedIterable<VersionDTO> findVersionsInRange(SessionDTO owner, long minOrdinal, long maxOrdinal, ModelType modelType, AccessMode accessMode) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

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
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

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

    public void setOrdinal(VersionDTO version) {
        if (version.getOrdinal() == null) {
            Long ordinal = interactionContextRepository.getOrdinal(version);
            logger.trace("Set ordinal of: [" + display(version) + "]: to: " + ordinal);
            //version.setOrdinal(ordinal);
        } else {
            logger.trace("Ordinal was already set: [" + version + "]");
        }
    }

    public boolean equals(VersionDTO thisVersion, Object that) {
        return equal(thisVersion, adapt(that, VersionDTO.class));
    }

    public int compareTo(VersionDTO thisVersion, Object that) {
        return thisVersion.compareTo(adapt(that, VersionDTO.class));
    }

}
