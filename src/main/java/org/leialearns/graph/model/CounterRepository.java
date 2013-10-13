package org.leialearns.graph.model;

import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface CounterRepository extends GraphRepository<CounterDTO> {
    CounterDTO findCounterByVersionAndNodeAndSymbol(VersionDTO version, NodeDTO node, SymbolDTO symbol);
}
