package org.leialearns.graph.model;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface FractionOracleRepository extends GraphRepository<FractionOracleDTO> {
    @Query("START fraction=node:oracleFractionIndex(index = {0}) RETURN fraction")
    FractionOracleDTO findByIndex(long index);
}
