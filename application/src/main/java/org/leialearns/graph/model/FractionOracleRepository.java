package org.leialearns.graph.model;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface FractionOracleRepository extends GraphRepository<FractionOracleDTO> {
    @Query("MATCH (fraction:FractionOracle) WHERE fraction.index = {0} RETURN fraction")
    FractionOracleDTO findByIndex(long index);
}
