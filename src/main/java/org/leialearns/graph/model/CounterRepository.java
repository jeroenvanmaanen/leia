package org.leialearns.graph.model;

import org.leialearns.graph.interaction.InteractionContextDTO;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface CounterRepository extends GraphRepository<CounterDTO> {
    Set<CounterDTO> findCountersByVersion(VersionDTO version);
    Set<CounterDTO> findCountersByVersionAndNode(VersionDTO version, NodeDTO node);
    CounterDTO findCounterByVersionAndNodeAndSymbol(VersionDTO version, NodeDTO node, SymbolDTO symbol);

    @Query("START target=node({0})" +
        " MATCH targetCounter-[:IN_VERSION]->target-[:IN_CONTEXT]->context" +
        " WITH context, targetCounter" +
        " MATCH targetCounter-[:FOR_NODE]->node<-[:FOR_NODE]-counterUpdate-[:IN_VERSION]->version-[:IN_CONTEXT]->context" +
        " WHERE version.ordinal >= {1} AND version.ordinal <= {2} AND version.modelTypeChar = 67" +
        " RETURN counterUpdate") // ord('C') == 67
    Set<CounterDTO> findUpdates(VersionDTO target, Long minVersion, Long maxVersion);

    @Query("START context=node({0}), target=node({1})" +
            " MATCH counterUpdate-[:IN_VERSION]->version-[:IN_CONTEXT]->context" +
            " WHERE version.ordinal >= {2} AND version.ordinal <= {3} AND version.modelTypeFlag = 67" +
            " RETURN counterUpdate") // ord('C') == 67
    Set<CounterDTO> findUpdates(InteractionContextDTO context, VersionDTO target, Long minVersion, Long maxVersion);

}
