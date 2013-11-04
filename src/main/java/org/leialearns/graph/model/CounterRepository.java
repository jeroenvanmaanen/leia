package org.leialearns.graph.model;

import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface CounterRepository extends GraphRepository<CounterDTO> {
    Set<CounterDTO> findCountersByVersion(VersionDTO version);
    CounterDTO findCounterByVersionAndNodeAndSymbol(VersionDTO version, NodeDTO node, SymbolDTO symbol);

    @Query("START target=node({0})" +
            " MATCH targetCounter-[:IN_VERSION]->target-[:IN_CONTEXT]->context" +
            " WITH context, targetCounter" +
            " MATCH targetCounter-[:FOR_NODE]->node-[:HAS_COUNTER]->counterUpdate-[:IN_VERSION]->version-[:IN_CONTEXT]->context" +
            " WHERE version.ordinal >= {1} AND version.ordinal <= {2}" +
            " RETURN counterUpdate")
    Set<CounterDTO> findUpdates(VersionDTO target, Long minVersion, Long maxVersion);
}
