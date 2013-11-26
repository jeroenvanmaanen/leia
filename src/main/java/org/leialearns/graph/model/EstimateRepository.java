package org.leialearns.graph.model;

import org.leialearns.graph.structure.NodeDTO;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface EstimateRepository extends GraphRepository<EstimateDTO> {
    Set<EstimateDTO> findEstimatesByVersionAndNode(VersionDTO version, NodeDTO node);
}
