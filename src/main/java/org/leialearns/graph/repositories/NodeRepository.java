package org.leialearns.graph.repositories;

import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.graph.structure.StructureDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface NodeRepository extends GraphRepository<NodeDTO> {
    @Query("START structure = node({0}), structureNode = node:nodePath(path={1}) MATCH structure-[:HAS_NODE]->structureNode RETURN structureNode")
    NodeDTO getNodeByStructureAndPath(StructureDTO structure, String path);
}
