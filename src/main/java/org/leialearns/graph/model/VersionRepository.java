package org.leialearns.graph.model;

import org.leialearns.graph.interaction.InteractionContextDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface VersionRepository extends GraphRepository<VersionDTO> {
    @Query("START context = node({0})" +
            " MATCH version-[:IN_CONTEXT]->context" +
            " WHERE version.ordinal = {1}" +
            " RETURN version")
    VersionDTO findByContextAndOrdinal(InteractionContextDTO context, Long ordinal);

    @Query("START context=node({0})" +
            " MATCH context-[:IN_CONTEXT]->version, version-[:NEXT_VERSION]->context" +
            " RETURN version")
    VersionDTO findFirstVersion(InteractionContextDTO interactionContext);

    @Query("START context=node({0})" +
            " MATCH version-[:IN_CONTEXT]->context, version-[:NEXT_VERSION]->context" +
            " RETURN version")
    VersionDTO findLastVersion(InteractionContextDTO interactionContext);

    @Query("START nextVersion=node({0})" +
            " MATCH nextVersion-[:IN_CONTEXT]->context, version-[:IN_CONTEXT]->context, version-[:NEXT_VERSION]->nextVersion" +
            " RETURN version")
    VersionDTO findPreviousVersion(VersionDTO versionDTO);

    @Query("START previousVersion=node({0})" +
            " MATCH previousVersion-[:IN_CONTEXT]->context, version-[:IN_CONTEXT]->context, previousVersion-[:NEXT_VERSION]->version" +
            " RETURN version")
    VersionDTO findNextVersion(VersionDTO versionDTO);

}
