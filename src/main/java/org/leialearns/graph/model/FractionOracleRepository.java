package org.leialearns.graph.model;

import org.springframework.data.neo4j.repository.GraphRepository;

public interface FractionOracleRepository extends GraphRepository<FractionOracleDTO> {
    FractionOracleDTO findByIndex(long index);
}
