package org.leialearns.graph.model;

import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface EstimateRepository extends GraphRepository<EstimateDTO> {
    Set<EstimateDTO> findEstimatesByVersionAndNode(VersionDTO version, NodeDTO node);

    @Query("START version = node({0}), node = node({1}), symbol = node({2})" +
            " MATCH version<-[:FOR_VERSION]-estimate-[:FOR_NODE]->node, estimate-[:FOR_SYMBOL]->symbol" +
            " RETURN estimate")
    EstimateDTO findEstimate(VersionDTO version, NodeDTO nodeDTO, SymbolDTO symbol);

    @Query("START version = node({0})" +
            " MATCH version<-[:FOR_VERSION]-estimate" +
            " RETURN estimate")
    Set<EstimateDTO> findEstimates(VersionDTO version);

    @Query("START version = node({0})" +
            " MATCH version<-[:FOR_VERSION]-estimate-[:FOR_NODE]->node" +
            " RETURN distinct node")
    Set<NodeDTO> findEstimateNodes(VersionDTO version);

    @Query("START version = node({0}), node = node({1})" +
            " MATCH version<-[v:FOR_VERSION]-estimate-[n:FOR_NODE]->node, symbol<-[s:FOR_SYMBOL]-estimate-[f:HAS_FRACTION]->fraction" +
            " DELETE v, n, s, f, estimate" +
            " RETURN count(estimate)")
    Integer deleteEstimates(VersionDTO version, NodeDTO node);

}
