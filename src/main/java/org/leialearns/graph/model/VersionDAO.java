package org.leialearns.graph.model;

import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.session.SessionDTO;
import org.leialearns.utilities.TypedIterable;

public class VersionDAO extends IdDaoSupport<VersionDTO> {

    public VersionDTO createVersion(SessionDTO owner, ModelType modelType) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public VersionDTO findVersion(SessionDTO owner, long ordinal) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public VersionDTO findExpected(CountedDTO counted) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public VersionDTO findLastVersion(SessionDTO owner, ModelType modelType, AccessMode accessMode) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
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
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void setAccessMode(VersionDTO version, AccessMode accessMode, SessionDTO session) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public CountedDTO createCountedVersion() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public CountedDTO createCountedVersion(VersionDTO version) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
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

    public boolean equals(VersionDTO thisVersion, Object that) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public int compareTo(VersionDTO thisVersion, Object that) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

}
