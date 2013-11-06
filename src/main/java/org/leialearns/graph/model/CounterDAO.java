package org.leialearns.graph.model;

import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.graph.interaction.InteractionContextDTO;
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
import java.util.Set;

import static org.leialearns.utilities.Display.asDisplay;
import static org.leialearns.utilities.Static.getLoggingClass;

public class CounterDAO extends IdDaoSupport<CounterDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    private CounterRepository repository;

    @Autowired
    private NodeDAO nodeDAO;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    public CounterDAO(CounterRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public TypedIterable<CounterDTO> findAll() {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<CounterDTO> findCounters(VersionDTO version) {
        return new TypedIterable<CounterDTO>(repository.findCountersByVersion(version), CounterDTO.class);
    }

    public TypedIterable<CounterDTO> findCounters(VersionDTO version, NodeDTO node) {
        return new TypedIterable<CounterDTO>(repository.findCountersByVersionAndNode(version, node), CounterDTO.class);
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
        VersionDTO version = counted.getVersion();
        return findOrCreateCounter(version, node, symbol);
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
        logger.debug("Copy counters: " + fromVersion + ": " + toVersion);
        Set<CounterDTO> counters = counterRepository.findCountersByVersion(fromVersion);
        logger.trace("Copying counters: {");
        for (CounterDTO counter : counters) {
            SymbolDTO symbolDTO = counter.getSymbol();
            CounterDTO copy = create(toVersion, counter.getNode(), symbolDTO);
            copy.setValue(counter.getValue());
            logger.trace("  Copy: [" + copy + "]");
        }
        logger.trace("}");
    }

    public void createCountersFromRecentCounted(ObservedDTO newObserved, ObservedDTO oldObserved) {
        logger.debug("Create counters from recent counted: [" + oldObserved + "] -> [" + newObserved + "]");
        if (oldObserved != null) {
            createCountersFromRecentCounted(oldObserved.getCountedVersion(), newObserved.getCountedVersion(), newObserved.getVersion());
        }
    }

    public void createCountersFromRecentCounted(VersionDTO previousVersion, VersionDTO lastVersion, VersionDTO toVersion) {
        if (lastVersion == null) {
            throw new IllegalArgumentException("Last version should not be null");
        }
        if (toVersion == null) {
            throw new IllegalArgumentException("To version should not be null");
        }
        Long minOrdinal = previousVersion == null ? 0 : previousVersion.getOrdinal() + 1;
        Long maxOrdinal = lastVersion.getOrdinal();
        logger.debug("Destination and bounds: {}, [{}, {}]", new Object[]{asDisplay(toVersion), minOrdinal, maxOrdinal});

        InteractionContextDTO interactionContext = toVersion.getInteractionContext();

        Set<VersionDTO> illegal = versionRepository.findUnreadable(interactionContext, ModelType.COUNTED.toChar(), minOrdinal, maxOrdinal);
        if (!illegal.isEmpty()) {
            for (VersionDTO versionDTO : illegal) {
                logger.warn("Not READABLE: [" + versionDTO + "]");
            }
            throw new IllegalStateException("Not all selected versions are READABLE: [" + previousVersion + "]: [" + lastVersion + "]");
        }

        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public TypedIterable<CounterUpdateDTO> findCounterUpdates(VersionDTO toVersion, VersionDTO previousVersion, VersionDTO lastVersion) {
        if (toVersion == null) {
            throw new IllegalArgumentException("To version should not be null");
        }
        long minOrdinal = previousVersion == null ? 0 : previousVersion.getOrdinal() + 1;
        Long maxOrdinal = lastVersion.getOrdinal();
        logger.debug("Destination and bounds: {}, [{}, {}]", new Object[]{asDisplay(toVersion), minOrdinal, maxOrdinal});

        Set<CounterDTO> updates = counterRepository.findUpdates(toVersion, minOrdinal, maxOrdinal);
        Map<String,CounterUpdateDTO> updateMap = new HashMap<>();
        for (CounterDTO update : updates) {
            String key = "" + update.getNode().getId() + "|" + update.getSymbol().getId();
            CounterUpdateDTO counterUpdate;
            if (updateMap.containsKey(key)) {
                counterUpdate = updateMap.get(key);
            } else {
                CounterDTO counter = counterRepository.findCounterByVersionAndNodeAndSymbol(toVersion, update.getNode(), update.getSymbol());
                counterUpdate = new CounterUpdateDTO(counter);
                updateMap.put(key, counterUpdate);
            }
            counterUpdate.increment(update.getValue());
        }

        return new TypedIterable<CounterUpdateDTO>(updateMap.values(), CounterUpdateDTO.class);
    }

}
