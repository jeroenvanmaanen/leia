package org.leialearns.graph.model;

import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface CounterRepository extends GraphRepository<CounterDTO> {
    Set<CounterDTO> findCountersByVersion(VersionDTO version);
    CounterDTO findCounterByVersionAndNodeAndSymbol(VersionDTO version, NodeDTO node, SymbolDTO symbol);
}
