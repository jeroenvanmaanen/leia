package org.leialearns.graph.model;

import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDAO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.structure.StructureDTO;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.leialearns.utilities.Static.getLoggingClass;

public class CounterDAO extends IdDaoSupport<CounterDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    private CounterRepository repository;

    @Autowired
    private NodeDAO nodeDAO;

    @Autowired
    public CounterDAO(CounterRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public TypedIterable<CounterDTO> findAll() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<CounterDTO> findCounters(VersionDTO version) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<CounterDTO> findCounters(VersionDTO version, NodeDTO node) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public CounterDTO findCounter(VersionDTO version, NodeDTO node, SymbolDTO symbol) {
        return repository.findCounterByVersionAndNodeAndSymbol(version, node, symbol);
    }

    public CounterDTO create(VersionDTO version, NodeDTO node, SymbolDTO symbol) {
        CounterDTO counter = new CounterDTO();
        counter.setVersion(version);
        counter.setNode(node);
        counter.setSymbol(symbol);
        counter.setValue(0l);
        counter = save(counter);
        return counter;
    }

    public CounterDTO findOrCreateCounter(VersionDTO version, NodeDTO node, SymbolDTO symbol) {
        CounterDTO counter = findCounter(version, node, symbol);
        if (counter == null) {
            counter = create(version, node, symbol);
        }
        return counter;
    }

    public CounterDTO getCounter(CountedDTO counted, TypedIterable<DirectedSymbolDTO> path, SymbolDTO symbol) {
        VersionDTO version = counted.getVersion();
        StructureDTO structureDTO = version.getInteractionContext().getStructure();
        NodeDTO node = nodeDAO.find(structureDTO, path);
        return findOrCreateCounter(version, node, symbol);
    }

    public CounterDTO getCounter(CountedDTO counted, NodeDTO node, SymbolDTO symbol) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void refresh(CounterDTO counter) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public void increment(final CounterDTO counter) {
        increment(counter, 1);
    }

    public void increment(final CounterDTO counter, final long amount) {
        logger.debug("Counter before increment: " + counter + " + " + amount);
        if (counter == null) {
            throw new IllegalArgumentException("The counter must not be null");
        }
        if (counter.getId() == null) {
            throw new IllegalArgumentException("The counter ID must not be null: " + counter);
        }
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("amount", amount);
        parameters.put("counter", counter.getId());
        Object newValue = repository.query("START counter = node({counter}) SET counter.value = counter.value + {amount} RETURN counter.value", parameters);
        logger.debug("Counter after increment: " + counter + ": " + newValue);
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
