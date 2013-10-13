package org.leialearns.graph.model;

import org.leialearns.graph.interaction.InteractionContextDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface VersionRepository extends GraphRepository<VersionDTO> {

    @Query("START context=node({0})" +
            " MATCH version-[:IN_CONTEXT]->context, version-[:NEXT_VERSION]->context" +
            " RETURN version")
    VersionDTO findLastVersion(InteractionContextDTO interactionContext);

    @Query("START nextVersion=node({0})" +
            " MATCH nextVersion-[:IN_CONTEXT]->context, version-[:IN_CONTEXT]->context, version-[:NEXT_VERSION]->nextVersion" +
            " RETURN version")
    VersionDTO findPreviousVersion(VersionDTO versionDTO);

}
