package org.leialearns.graph.repositories;

import org.leialearns.graph.interaction.SymbolDTO;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface SymbolRepository extends GraphRepository<SymbolDTO> {
}
