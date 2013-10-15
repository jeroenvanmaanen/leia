package org.leialearns.graph.structure;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface NodeRepository extends GraphRepository<NodeDTO> {
    Set<NodeDTO> findNodesByStructure(StructureDTO structure);

    @Query("START structure = node({0}), structureNode = node:nodePath(path={1}) MATCH structure-[:HAS_NODE]->structureNode RETURN structureNode")
    NodeDTO getNodeByStructureAndPath(StructureDTO structure, String path);
}
