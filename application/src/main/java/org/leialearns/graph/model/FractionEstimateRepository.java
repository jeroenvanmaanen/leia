package org.leialearns.graph.model;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface FractionEstimateRepository extends GraphRepository<FractionEstimateDTO> {
    @Query("START fraction = node:__types__(className=\"org.leialearns.graph.model.FractionEstimateDTO\")" +
            " WHERE NOT fraction<-[:HAS_FRACTION]-()" +
            " WITH fraction" +
            " DELETE fraction" +
            " RETURN count(fraction)")
    Integer deleteUnusedFractions();
}
