package org.leialearns.graph.model;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface ExpectedRepository extends GraphRepository<ExpectedDTO> {
    @Query("START version = node({0}) MATCH expected-[:EXTENDS]->version RETURN expected")
    ExpectedDTO findExpectedByVersion(VersionDTO version);
}
