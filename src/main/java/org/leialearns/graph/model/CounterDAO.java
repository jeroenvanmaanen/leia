package org.leialearns.graph.model;

import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.utilities.TypedIterable;

public class CounterDAO extends IdDaoSupport<CounterDTO> {

    public TypedIterable<CounterDTO> findAll() {
        return null; // TODO: implement
    }

    public TypedIterable<CounterDTO> findCounters(VersionDTO version) {
        return null; // TODO: implement
    }

    public TypedIterable<CounterDTO> findCounters(VersionDTO version, NodeDTO node) {
        return null; // TODO: implement
    }

    public CounterDTO findCounter(VersionDTO version, NodeDTO node, SymbolDTO symbolDTO) {
        return null; // TODO: implement
    }

    public CounterDTO create(VersionDTO version, NodeDTO node, SymbolDTO symbol) {
        return null; // TODO: implement
    }

    public CounterDTO findOrCreateCounter(VersionDTO version, NodeDTO node, SymbolDTO symbol) {
        return null; // TODO: implement
    }

    public CounterDTO getCounter(CountedDTO counted, TypedIterable<DirectedSymbolDTO> path, SymbolDTO symbol) {
        return null; // TODO: implement
    }

    public CounterDTO getCounter(CountedDTO counted, NodeDTO node, SymbolDTO symbol) {
        return null; // TODO: implement
    }

    public void refresh(CounterDTO counter) {
        // TODO: implement
    }

    public void increment(final CounterDTO counter) {
        // TODO: implement
    }

    public void increment(final CounterDTO counter, final long amount) {
        // TODO: implement
    }

    public void copyCounters(VersionDTO fromVersion, VersionDTO toVersion) {
        // TODO: implement
    }

    public void createCountersFromRecentCounted(ObservedDTO newObserved, ObservedDTO oldObserved) {
        // TODO: implement
    }

    public void createCountersFromRecentCounted(VersionDTO previousVersion, VersionDTO lastVersion, VersionDTO toVersion) {
        // TODO: implement
    }

    public TypedIterable<CounterUpdateDTO> findCounterUpdates(VersionDTO toVersion, VersionDTO previousVersion, VersionDTO lastVersion) {
        return null; // TODO: implement
    }

}
