package org.leialearns.graph.repositories;

import org.leialearns.graph.structure.StructureDTO;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface StructureRepository extends GraphRepository<StructureDTO> {
    StructureDTO getStructureByUri(String uri);
}
