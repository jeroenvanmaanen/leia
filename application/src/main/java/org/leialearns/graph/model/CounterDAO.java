package org.leialearns.graph.model;

import com.google.common.base.Function;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.bridge.FactoryAccessor;
import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.interaction.SymbolRepository;
import org.leialearns.graph.structure.NodeDAO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.structure.NodeRepository;
import org.leialearns.graph.structure.StructureDTO;
import org.leialearns.logic.model.CounterLogger;
import org.leialearns.logic.model.Version;
import org.leialearns.logic.structure.Node;
import org.leialearns.logic.utilities.Static;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Boolean.TRUE;
import static org.leialearns.bridge.Static.getFarObject;
import static org.leialearns.utilities.Display.asDisplay;
import static org.leialearns.utilities.Display.show;
import static org.leialearns.utilities.Static.getLoggingClass;

public class CounterDAO extends IdDaoSupport<CounterDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private CounterRepository repository;

    @Autowired
    private CounterLogger counterLogger;

    @Autowired
    private NodeDAO nodeDAO;

    @Autowired
    private NodeRepository nodeRepository;

    public FactoryAccessor<Node> nodeFactoryAccessor = new FactoryAccessor<>(Node.class);

    @Autowired
    private VersionRepository versionRepository;

    public FactoryAccessor<Version> versionFactoryAccessor = new FactoryAccessor<>(Version.class);

    @Autowired
    private SymbolRepository symbolRepository;

    @Override
    protected CounterRepository getRepository() {
        return repository;
    }

    @BridgeOverride
    public TypedIterable<CounterDTO> findCounters(VersionDTO version) {
        return new TypedIterable<>(repository.findCountersByVersion(version), CounterDTO.class);
    }

    @BridgeOverride
    public TypedIterable<CounterDTO> findCounters(VersionDTO version, NodeDTO node) {
        return new TypedIterable<>(repository.findCountersByVersionAndNode(version, node), CounterDTO.class);
    }

    @BridgeOverride
    public TypedIterable<CounterDTO> findCounters(VersionDTO version, Function<Node,Node.Iterable> getChildren, Function<Node,Boolean> getInclude) {
        Collection<CounterDTO> result = new ArrayList<>();
        if (getChildren == null) {
            Map<NodeDTO,Boolean> includeCache = new HashMap<>();
            for (CounterDTO counter : findCounters(version)) {
                NodeDTO node = counter.getNode();
                boolean included;
                if (includeCache.containsKey(node)) {
                    included = includeCache.get(node);
                } else {
                    Node near = nodeFactoryAccessor.getNearObject(node);
                    included = getInclude.apply(near) == TRUE;
                    includeCache.put(node, included);
                }
                if (included) {
                    result.add(counter);
                }
            }
        } else {
            StructureDTO structure = version.getInteractionContext().getStructure();
            for (NodeDTO node : nodeDAO.findRootNodes(structure)) {
                findCounters(version, getChildren, getInclude, node, result);
            }
        }
        return new TypedIterable<>(result, CounterDTO.class);
    }

    protected void findCounters(VersionDTO version, Function<Node,Node.Iterable> getChildren, Function<Node,Boolean> getInclude, NodeDTO node, Collection<CounterDTO> result) {
        Node near = nodeFactoryAccessor.getNearObject(node);
        if (getInclude.apply(near) == TRUE) {
            for (CounterDTO counter : findCounters(version, node)) {
                result.add(counter);
            }
        }
        for (Node nearChild : Static.notNull(getChildren.apply(near))) {
            NodeDTO child = getFarObject(nearChild, NodeDTO.class);
            findCounters(version, getChildren, getInclude, child, result);
        }
    }

    public CounterDTO findCounter(VersionDTO version, NodeDTO node, SymbolDTO symbol) {
        CounterDTO result;
        logger.debug("Find counter: {}: {}: {}", new Object[] { version, node, symbol });
        if (version == null || node == null || symbol == null) {
            result = null;
        } else {
            result = repository.findCounterByVersionAndNodeAndSymbol(version, node, symbol);
        }
        return result;
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

    @BridgeOverride
    public CounterDTO getCounter(CountedDTO counted, TypedIterable<DirectedSymbolDTO> path, SymbolDTO symbol) {
        VersionDTO version = counted.getVersion();
        StructureDTO structureDTO = version.getInteractionContext().getStructure();
        NodeDTO node = nodeDAO.find(structureDTO, path);
        return findOrCreateCounter(version, node, symbol);
    }

    @BridgeOverride
    public CounterDTO getCounter(CountedDTO counted, NodeDTO node, SymbolDTO symbol) {
        VersionDTO version = counted.getVersion();
        return findOrCreateCounter(version, node, symbol);
    }

    @BridgeOverride
    public void refresh(CounterDTO counter) {
        logger.trace("Refresh ignored for: {}", counter);
    }

    @BridgeOverride
    public void increment(final CounterDTO counter) {
        increment(counter, 1);
    }

    public void increment(final CounterDTO counter, final long amount) {
        logger.trace("Counter before increment: " + counter + " + " + amount);
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
        logger.trace("Counter after increment: " + counter + ": " + newValue);
    }

    public void copyCounters(VersionDTO fromVersion, VersionDTO toVersion) {
        logger.debug("Copy counters: " + fromVersion + ": " + toVersion);
        Set<CounterDTO> counters = repository.findCountersByVersion(fromVersion);
        logger.trace("Copying counters: {");
        for (CounterDTO counter : counters) {
            SymbolDTO symbolDTO = counter.getSymbol();
            CounterDTO copy = create(toVersion, counter.getNode(), symbolDTO);
            copy.setValue(counter.getValue());
            logger.trace("  Copy: [" + copy + "]");
        }
        logger.trace("}");
    }

    @BridgeOverride
    public void createCountersFromRecentCounted(ObservedDTO newObserved, ObservedDTO oldObserved) {
        logger.debug("Create counters from recent counted: [" + oldObserved + "] -> [" + newObserved + "]");
        VersionDTO previousVersion;
        if (oldObserved == null) {
            previousVersion = null;
        } else {
            previousVersion = oldObserved.getCountedVersion();
        }
        createCountersFromRecentCounted(previousVersion, newObserved.getCountedVersion(), newObserved.getVersion());
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

        Set<Map<String,Object>> missing = versionRepository.findMissing(interactionContext, ModelType.COUNTED.toChar(), minOrdinal, maxOrdinal);
        ObjectCache<NodeDTO> nodeCache = new ObjectCache<>("Nodes", new Function<Long, NodeDTO>() {
            @Override
            public NodeDTO apply(Long id) {
                return nodeRepository.findById(id);
            }
        });
        ObjectCache<SymbolDTO> symbolCache = new ObjectCache<>("Symbols", new Function<Long, SymbolDTO>() {
            @Override
            public SymbolDTO apply(Long id) {
                return symbolRepository.findById(id);
            }
        });
        logger.debug("Missing: {");
        for (Map<String,Object> pair : missing) {
            logger.trace("  Pair: {}", asDisplay(pair));
            NodeDTO node = nodeCache.get(pair.get("node_id"));
            SymbolDTO symbol = symbolCache.get(pair.get("symbol_id"));
            CounterDTO exists = repository.findCounterByVersionAndNodeAndSymbol(toVersion, node, symbol);
            if (exists == null) {
                logger.debug("  " + node + " [" + show(symbol.getDenotation()) + "]");
                create(toVersion, node, symbol);
            } else if (logger.isTraceEnabled()) {
                logger.trace("  Skipped: exists: {}", exists);
            }
        }
        logger.debug("}");
    }

    public TypedIterable<CounterUpdateDTO> findCounterUpdates(VersionDTO toVersion, VersionDTO previousVersion, VersionDTO lastVersion) {
        if (toVersion == null) {
            throw new IllegalArgumentException("To version should not be null");
        }
        long minOrdinal = previousVersion == null ? 0 : previousVersion.getOrdinal() + 1;
        Long maxOrdinal = lastVersion.getOrdinal();
        logger.debug("Destination and bounds: {}, [{}, {}]", new Object[]{asDisplay(toVersion), minOrdinal, maxOrdinal});
        if (logger.isTraceEnabled()) {
            Set<VersionDTO> range = versionRepository.findRange(toVersion.getInteractionContext(), minOrdinal, maxOrdinal);
            List<Version> countedVersions = new ArrayList<>();
            countedVersions.add(versionFactoryAccessor.getNearObject(toVersion));
            logger.trace("Range: {");
            for (VersionDTO version : range) {
                logger.trace("  {}", version);
                if (version.getModelType() == ModelType.COUNTED) {
                    countedVersions.add(versionFactoryAccessor.getNearObject(version));
                }
            }
            logger.trace("}");
            counterLogger.logCounters(countedVersions.toArray(new Version[countedVersions.size()]));
        }

        InteractionContextDTO context = toVersion.getInteractionContext();
        logger.debug("Find updates: {}: {}: {}: {}", new Object[]{context, toVersion, minOrdinal, maxOrdinal});
        Set<CounterDTO> updates = repository.findUpdates(toVersion.getInteractionContext(), toVersion, minOrdinal, maxOrdinal);
        Map<String,CounterUpdateDTO> updateMap = new HashMap<>();
        for (CounterDTO update : updates) {
            String key = "" + update.getNode().getId() + "|" + update.getSymbol().getId();
            CounterUpdateDTO counterUpdate;
            if (updateMap.containsKey(key)) {
                counterUpdate = updateMap.get(key);
            } else {
                logger.debug("Create new update for: #{} <- (#{}, {}, {})", new Object[]{toVersion.getOrdinal(), update.getVersion().getOrdinal(), update.getNode(), update.getSymbol()});
                CounterDTO counter = repository.findCounterByVersionAndNodeAndSymbol(toVersion, update.getNode(), update.getSymbol());
                counterUpdate = new CounterUpdateDTO(counter);
                updateMap.put(key, counterUpdate);
            }
            logger.trace("Register update: {}: {}", key, update);
            counterUpdate.increment(update.getValue());
        }

        return new TypedIterable<>(updateMap.values(), CounterUpdateDTO.class);
    }

}
