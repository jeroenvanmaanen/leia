package org.leialearns.graph.interaction;

import org.leialearns.graph.model.VersionDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface InteractionContextRepository extends GraphRepository<InteractionContextDTO> {

    @Query("MATCH (interactionContext:InteractionContext) WHERE interactionContext.uri = {0} RETURN interactionContext")
    InteractionContextDTO getInteractionContextByUri(String uri);

    @Query("START context=node({0}) MATCH context-[:HAS_ACTIONS]->alphabet RETURN alphabet")
    Set<AlphabetDTO> getActions(InteractionContextDTO context);

    @Query("Start context=node({0}) MATCH context<-[:IN_CONTEXT]-(version:Version) RETURN version")
    Set<VersionDTO> getVersions(InteractionContextDTO context);
}
