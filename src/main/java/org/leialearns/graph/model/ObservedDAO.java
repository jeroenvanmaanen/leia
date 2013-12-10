package org.leialearns.graph.model;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.session.SessionDTO;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.utilities.Display.asDisplay;
import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.getLoggingClass;

public class ObservedDAO extends IdDaoSupport<ObservedDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    VersionDAO versionDAO;

    @Autowired
    CounterDAO counterDAO;

    @Autowired
    public ObservedDAO(ObservedRepository repository) {
        super(repository);
    }

    public ObservedDTO find(VersionDTO version) {
        ObservedDTO result = findMaybe(version);
        if (result == null) {
            throw new IllegalStateException("Observed details missing: " + display(version));
        }
        return result;
    }

    public ObservedDTO create(VersionDTO version) {
        logger.debug("Create extension for version: [" + version + "]: #" + version.getId());
        ObservedDTO dto = new ObservedDTO();
        dto.setVersion(version);
        logger.trace("Version of DTO: [" + dto.getVersion() + "]");
        dto = save(dto);
        return dto;
    }

    public ObservedDTO findOrCreate(VersionDTO version) {
        ObservedDTO result = findMaybe(version);
        if (result == null) {
            result = create(version);
        }
        return result;
    }

    protected ObservedDTO findMaybe(VersionDTO version) {
        logger.debug("Find extension for version: [" + version + "]: #" + version.getId());
        ObservedDTO result = findDTO("START version = node({p1}) MATCH version<-[:EXTENDS]-observed RETURN observed LIMIT 1", version.getId());
        logger.debug("Observed: {}: extends: {}", new Object[]{result,version});
        return result;
    }

    @BridgeOverride
    public VersionDTO getOrCreateDelta(ObservedDTO observed) {
        VersionDTO result = observed.getDeltaVersion();
        if (result == null) {
            VersionDTO observedVersion = observed.getVersion();
            SessionDTO owner = observedVersion.getOwner();
            result = versionDAO.createVersion(owner, ModelType.DELTA);
            observed.setDeltaVersion(result);
            save(observed);
        }
        return result;
    }

    @BridgeOverride
    public CountedDTO getCounted(ObservedDTO observed) {
        VersionDTO version = observed.getCountedVersion();
        CountedDTO result;
        if (version == null) {
            result = null;
        } else {
            result = versionDAO.createCountedVersion(version);
        }
        return result;
    }

    @BridgeOverride
    public void attachCounted(ObservedDTO newObserved, ObservedDTO oldObserved) {
        VersionDTO newObservedVersion = newObserved.getVersion();
        logger.debug("New observed version (!): {}", asDisplay(newObservedVersion));
        VersionDTO oldObservedCountedVersion = getCountedVersion(oldObserved);
        logger.debug("Old observed counted version: {}", asDisplay(oldObservedCountedVersion));
        VersionDTO version = versionDAO.findRangeMax(newObservedVersion, oldObservedCountedVersion, ModelType.COUNTED, AccessMode.READABLE);
        logger.debug("Attach counted version: [" + version + "]");
        newObserved.setCountedVersion(version);
        logger.debug("Get counted: [" + newObserved.getCountedVersion() + "]");
    }

    @BridgeOverride
    public void attachToggled(ObservedDTO observed) {
        VersionDTO newVersion = observed.getVersion();
        VersionDTO versionDTO = versionDAO.findLastBefore(newVersion, ModelType.OBSERVED, AccessMode.READABLE);
        ObservedDTO oldObserved = (versionDTO == null ? null : versionDAO.createObservedVersion(versionDTO));
        ToggledDTO oldToggled = (oldObserved == null ? null : oldObserved.getToggled());
        VersionDTO oldToggleVersion = (oldToggled == null ? null : oldToggled.getVersion());
        VersionDTO newToggledVersion = versionDAO.findRangeMax(observed.getVersion(), oldToggleVersion, ModelType.TOGGLED, AccessMode.READABLE);
        ToggledDTO toggledDTO = versionDAO.findToggledVersion(newToggledVersion);
        logger.debug("New toggled: [" + toggledDTO + "]");
        observed.setToggled(toggledDTO);
        save(observed);
    }

    @BridgeOverride
    public void copyCountersFromLastObserved(ObservedDTO toObserved, VersionDTO lastObserved) {
        VersionDTO toVersion = toObserved.getVersion();
        copyCountersFromLastObserved(lastObserved, toVersion);
    }

    @BridgeOverride
    public void copyCountersFromLastObserved(VersionDTO lastObserved, VersionDTO toVersion) {
        logger.debug("Copy counters from last observed: [" + lastObserved + "] -> [" + toVersion + "]");
        counterDAO.copyCounters(lastObserved, toVersion);
    }

    @BridgeOverride
    public TypedIterable<CounterUpdateDTO> findCounterUpdates(ObservedDTO newObserved, ObservedDTO oldObserved) {
        VersionDTO oldObservedVersion = (oldObserved == null ? null : oldObserved.getVersion());
        VersionDTO lastVersion = versionDAO.findRangeMax(newObserved.getVersion(), oldObservedVersion, ModelType.COUNTED, AccessMode.READABLE);
        return counterDAO.findCounterUpdates(newObserved.getVersion(), oldObservedVersion, lastVersion);
    }

    @BridgeOverride
    public void copyCountersFromLastDelta(ObservedDTO newObserved, ObservedDTO oldObserved) {
        logger.debug("Copy counters from last delta: [" + oldObserved + "] -> [" + newObserved + "]");
        if (oldObserved != null) {
            VersionDTO oldDelta = oldObserved.getDeltaVersion();
            VersionDTO newDelta = newObserved.getDeltaVersion();
            counterDAO.copyCounters(oldDelta, newDelta);
        }
    }

    protected VersionDTO getCountedVersion(ObservedDTO observed) {
        return observed == null ? null : observed.getCountedVersion();
    }

}
