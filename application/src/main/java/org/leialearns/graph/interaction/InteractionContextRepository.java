package org.leialearns.graph.interaction;

import org.leialearns.graph.model.VersionDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface InteractionContextRepository extends GraphRepository<InteractionContextDTO> {
    @Query("MATCH (interactionContext:InteractionContext) WHERE interactionContext.uri = {0} RETURN interactionContext")
    InteractionContextDTO getInteractionContextByUri(String uri);

    @Query("START context=node({0}) CREATE context-[new:NEXT_VERSION]->context RETURN true")
    boolean setEmptyVersionChain(InteractionContextDTO context);

    @Query("START version=node({0})" +
            " MATCH version-[:IN_CONTEXT]->context, before-[old:NEXT_VERSION]->context" +
            " CREATE UNIQUE before-[:NEXT_VERSION]->version-[:NEXT_VERSION]->context" +
            " SET version.ordinal = coalesce(before.ordinal, -1) + 1" +
            " DELETE old" +
            " RETURN version.ordinal")
    Long getOrdinal(VersionDTO version);

    @Query("START context=node({0}) MATCH context-[:HAS_ACTIONS]->alphabet RETURN alphabet")
    Set<AlphabetDTO> getActions(InteractionContextDTO context);

    @Query("START context=node({0}) MATCH context-[:NEXT_VERSION]->(version:Version) RETURN version")
    Set<VersionDTO> getNextVersions(InteractionContextDTO context);
}
