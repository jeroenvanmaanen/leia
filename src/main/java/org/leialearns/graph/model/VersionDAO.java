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

import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.equal;
import static org.leialearns.utilities.Static.getLoggingClass;

public class VersionDAO extends IdDaoSupport<VersionDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final VersionRepository versionRepository;

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
        result.setOwner(owner);
        result.setInteractionContext(interactionContext);
        result.setModelType(modelType);
        result.setAccessMode(AccessMode.LOCKED);
        save(result);
        setOrdinal(result);
        logger.debug("Created version: " + result);
        return result;
    }

    public VersionDTO findVersion(SessionDTO owner, long ordinal) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public VersionDTO findExpected(CountedDTO counted) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public VersionDTO findLastVersion(SessionDTO owner, ModelType modelType, AccessMode accessMode) {
        VersionDTO version = versionRepository.findLastVersion(owner.getInteractionContext());
        while (version != null && version.getId() != null) {
            AccessMode versionAccessMode = version.getAccessMode();
            if ((modelType == null || version.getModelType() == modelType) && (accessMode == null ? versionAccessMode != AccessMode.EXCLUDE : versionAccessMode == accessMode)) {
                break;
            }
            version = versionRepository.findPreviousVersion(version);
        }
        if (version != null && version.getId() == null) {
            version = null;
        }
        return version;
    }

    public VersionDTO findLastBefore(VersionDTO version, ModelType modelType, AccessMode accessMode) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public VersionDTO findRangeMax(VersionDTO lastVersion, VersionDTO previousVersion, ModelType modelType, AccessMode accessMode) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
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
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
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
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public ToggledDTO findToggledVersion(VersionDTO version) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void waitForLock(VersionDTO versionDTO, SessionDTO session) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
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
