package org.leialearns.graph.structure;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface NodeRepository extends GraphRepository<NodeDTO> {
    NodeDTO findById(Long id);
    Set<NodeDTO> findNodesByStructure(StructureDTO structure);

    @Query("START structure = node({0}) MATCH structure-[:HAS_ROOT]->rootNode RETURN rootNode")
    Set<NodeDTO> findRootNodes(StructureDTO structure);

    @Query("START parent = node({0}) MATCH parent-[:HAS_CHILD]->child RETURN child")
    Set<NodeDTO> findChildren(NodeDTO node);

    @Query("START structure = node({0}), structureNode = node:nodePath(path={1}) MATCH structure-[:HAS_NODE]->structureNode RETURN structureNode")
    NodeDTO getNodeByStructureAndPath(StructureDTO structure, String path);
}
