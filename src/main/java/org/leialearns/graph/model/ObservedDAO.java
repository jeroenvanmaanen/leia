package org.leialearns.graph.model;

import org.leialearns.graph.IdDaoSupport;
import org.leialearns.utilities.TypedIterable;

public class ObservedDAO extends IdDaoSupport<ObservedDTO> {

    public ObservedDTO find(VersionDTO version) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public ObservedDTO create(VersionDTO version) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public ObservedDTO findOrCreate(VersionDTO version) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public VersionDTO getOrCreateDelta(ObservedDTO observed) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public CountedDTO getCounted(ObservedDTO observed) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void attachCounted(ObservedDTO newObserved, ObservedDTO oldObserved) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
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

}
