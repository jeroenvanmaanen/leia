package org.leialearns.graph.model;

import org.leialearns.graph.IdDaoSupport;
import org.leialearns.utilities.TypedIterable;

public class ObservedDAO extends IdDaoSupport<ObservedDTO> {

    public ObservedDTO find(VersionDTO version) {
        return null; // TODO: implement
    }

    public ObservedDTO create(VersionDTO version) {
        return null; // TODO: implement
    }

    public ObservedDTO findOrCreate(VersionDTO version) {
        return null; // TODO: implement
    }

    public VersionDTO getOrCreateDelta(ObservedDTO observed) {
        return null; // TODO: implement
    }

    public CountedDTO getCounted(ObservedDTO observed) {
        return null; // TODO: implement
    }

    public void attachCounted(ObservedDTO newObserved, ObservedDTO oldObserved) {
        // TODO: implement
    }

    public void attachToggled(ObservedDTO observed) {
        // TODO: implement
    }

    public void attachExpected(ObservedDTO observed) {
        // TODO: implement
    }

    public void copyCountersFromLastObserved(ObservedDTO toObserved, VersionDTO lastObserved) {
        // TODO: implement
    }

    public void copyCountersFromLastObserved(VersionDTO lastObserved, VersionDTO toVersion) {
        // TODO: implement
    }

    public TypedIterable<CounterUpdateDTO> findCounterUpdates(ObservedDTO newObserved, ObservedDTO oldObserved) {
        return null; // TODO: implement
    }

    public void copyCountersFromLastDelta(ObservedDTO newObserved, ObservedDTO oldObserved) {
        // TODO: implement
    }

}
