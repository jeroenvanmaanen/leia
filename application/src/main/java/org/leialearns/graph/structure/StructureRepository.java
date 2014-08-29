package org.leialearns.graph.structure;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface StructureRepository extends GraphRepository<StructureDTO> {
    @Query("MATCH (structure:Structure) WHERE structure.uri = {0} RETURN structure")
    StructureDTO getStructureByUri(String uri);

    @Query("START structure = node({0}) WHERE not(has(structure.maxDepth)) OR structure.maxDepth < {1} SET structure.maxDepth = {1} RETURN structure.maxDepth")
    Integer updateMaxDepth(StructureDTO structure, Integer newMaxDepth);
}
