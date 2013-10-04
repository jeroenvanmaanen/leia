package org.leialearns.graph.repositories;

import org.leialearns.graph.structure.StructureDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface StructureRepository extends GraphRepository<StructureDTO> {
    StructureDTO getStructureByUri(String uri);

    @Query("START structure = node({0}) WHERE structure.maxDepth? < {1} SET structure.maxDepth = {1} RETURN structure.maxDepth")
    Integer updateMaxDepth(StructureDTO structure, Integer newMaxDepth);
}
