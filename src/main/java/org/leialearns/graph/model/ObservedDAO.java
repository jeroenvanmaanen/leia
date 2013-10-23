package org.leialearns.graph.model;

import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.utilities.Static.getLoggingClass;

public class ObservedDAO extends IdDaoSupport<ObservedDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    VersionDAO versionDAO;

    @Autowired
    public ObservedDAO(ObservedRepository repository) {
        super(repository);
    }

    public ObservedDTO find(VersionDTO version) {
        logger.debug("Find extension for version: [" + version + "]: #" + version.getId());
        ObservedDTO result = findDTO("START version = node({p1}) MATCH version<-[:EXTENDS]-observed RETURN observed LIMIT 1", version.getId());
        logger.debug("Observed: {}: extends: {}", new Object[]{result,version});
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
        ObservedDTO result = find(version);
        if (result == null) {
            result = create(version);
        }
        return result;
    }

    public VersionDTO getOrCreateDelta(ObservedDTO observed) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

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

    public void attachCounted(ObservedDTO newObserved, ObservedDTO oldObserved) {
        VersionDTO newObservedVersion = newObserved.getVersion();
        logger.debug("New observed version (!): [" + newObservedVersion + "]");
        VersionDTO version = versionDAO.findRangeMax(newObservedVersion, getCountedVersion(oldObserved), ModelType.COUNTED, AccessMode.READABLE);
        logger.debug("Attach counted version: [" + version + "]");
        newObserved.setCountedVersion(version);
        logger.debug("Get counted: [" + newObserved.getCountedVersion() + "]");
    }

    public void attachToggled(ObservedDTO observed) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void attachExpected(ObservedDTO observed) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void copyCountersFromLastObserved(ObservedDTO toObserved, VersionDTO lastObserved) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void copyCountersFromLastObserved(VersionDTO lastObserved, VersionDTO toVersion) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<CounterUpdateDTO> findCounterUpdates(ObservedDTO newObserved, ObservedDTO oldObserved) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void copyCountersFromLastDelta(ObservedDTO newObserved, ObservedDTO oldObserved) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    protected VersionDTO getCountedVersion(ObservedDTO observed) {
        return observed == null ? null : observed.getCountedVersion();
    }

}
