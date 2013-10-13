package org.leialearns.graph.interaction;

import org.leialearns.graph.model.VersionDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface InteractionContextRepository extends GraphRepository<InteractionContextDTO> {
    InteractionContextDTO getInteractionContextByUri(String uri);

    @Query("START context=node({0}) CREATE UNIQUE context-[new:NEXT_VERSION]->context RETURN true")
    boolean setEmptyVersionChain(InteractionContextDTO context);

    @Query("START version=node({0})" +
            " MATCH version-[:IN_CONTEXT]->context, before-[old:NEXT_VERSION]->context" +
            " CREATE UNIQUE before-[:NEXT_VERSION]->version-[:NEXT_VERSION]->context" +
            " SET version.ordinal = coalesce(before.ordinal?, -1) + 1" +
            " DELETE old" +
            " RETURN version.ordinal")
    Long getOrdinal(VersionDTO version);

}
