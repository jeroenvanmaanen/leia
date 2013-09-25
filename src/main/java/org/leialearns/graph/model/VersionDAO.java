package org.leialearns.graph.model;

import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.session.SessionDTO;
import org.leialearns.utilities.TypedIterable;

public class VersionDAO extends IdDaoSupport<VersionDTO> {

    public VersionDTO createVersion(SessionDTO owner, ModelType modelType) {
        return null; // TODO: implement
    }

    public VersionDTO findVersion(SessionDTO owner, long ordinal) {
        return null; // TODO: implement
    }

    public VersionDTO findExpected(CountedDTO counted) {
        return null; // TODO: implement
    }

    public VersionDTO findLastVersion(SessionDTO owner, ModelType modelType, AccessMode accessMode) {
        return null; // TODO: implement
    }

    public VersionDTO findLastBefore(VersionDTO version, ModelType modelType, AccessMode accessMode) {
        return null; // TODO: implement
    }

    public VersionDTO findRangeMax(VersionDTO lastVersion, VersionDTO previousVersion, ModelType modelType, AccessMode accessMode) {
        return null; // TODO: implement
    }

    public TypedIterable<VersionDTO> findVersionsInRange(SessionDTO owner, long minOrdinal, long maxOrdinal, ModelType modelType, AccessMode accessMode) {
        return null; // TODO: implement
    }

    public VersionDTO findOrCreateLastVersion(SessionDTO owner, ModelType modelType, AccessMode accessMode) {
        return null; // TODO: implement
    }

    public void setAccessMode(VersionDTO version, AccessMode accessMode, SessionDTO session) {
        // TODO: implement
    }

    public CountedDTO createCountedVersion() {
        return null; // TODO: implement
    }

    public CountedDTO createCountedVersion(VersionDTO version) {
        return null; // TODO: implement
    }

    public ObservedDTO createObservedVersion(VersionDTO version) {
        return null; // TODO: implement
    }

    public ToggledDTO findToggledVersion(VersionDTO version) {
        return null; // TODO: implement
    }

    public void waitForLock(VersionDTO versionDTO, SessionDTO session) throws InterruptedException {
        // TODO: implement
    }

    public boolean equals(VersionDTO thisVersion, Object that) {
        return false; // TODO: implement
    }

    public int compareTo(VersionDTO thisVersion, Object that) {
        return 0; // TODO: implement
    }

}
