package org.leialearns.graph.model;

import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.utilities.TypedIterable;

public class CounterDAO extends IdDaoSupport<CounterDTO> {

    public TypedIterable<CounterDTO> findAll() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<CounterDTO> findCounters(VersionDTO version) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<CounterDTO> findCounters(VersionDTO version, NodeDTO node) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public CounterDTO findCounter(VersionDTO version, NodeDTO node, SymbolDTO symbolDTO) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public CounterDTO create(VersionDTO version, NodeDTO node, SymbolDTO symbol) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public CounterDTO findOrCreateCounter(VersionDTO version, NodeDTO node, SymbolDTO symbol) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public CounterDTO getCounter(CountedDTO counted, TypedIterable<DirectedSymbolDTO> path, SymbolDTO symbol) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public CounterDTO getCounter(CountedDTO counted, NodeDTO node, SymbolDTO symbol) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void refresh(CounterDTO counter) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void increment(final CounterDTO counter) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void increment(final CounterDTO counter, final long amount) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void copyCounters(VersionDTO fromVersion, VersionDTO toVersion) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void createCountersFromRecentCounted(ObservedDTO newObserved, ObservedDTO oldObserved) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void createCountersFromRecentCounted(VersionDTO previousVersion, VersionDTO lastVersion, VersionDTO toVersion) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<CounterUpdateDTO> findCounterUpdates(VersionDTO toVersion, VersionDTO previousVersion, VersionDTO lastVersion) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

}
