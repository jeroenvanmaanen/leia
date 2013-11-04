package org.leialearns.graph.model;

import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.interaction.InteractionContextDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface VersionRepository extends GraphRepository<VersionDTO> {
    @Query("START context = node({0})" +
            " MATCH version-[:IN_CONTEXT]->context" +
            " WHERE version.ordinal = {1}" +
            " RETURN version")
    VersionDTO findByContextAndOrdinal(InteractionContextDTO context, Long ordinal);

    @Query("START context=node({0})" +
            " MATCH version-[:IN_CONTEXT]->context, context-[:NEXT_VERSION]->version" +
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

    @Query("START context=node({0})" +
            " MATCH version-[:IN_CONTEXT]->context" +
            " WHERE version.modelTypeFlag = {1}, version.ordinal >= {2}, version.ordinal <= {3}, version.accessModeFlag <> 'X', version.accessModeFlag <> 'R'" +
            " RETURN version")
    Set<VersionDTO> findUnreadable(InteractionContextDTO context, char modelTypeChar, Long minOrdinal, Long maxOrdinal);

    @Query("START context=node({0})" +
            " MATCH version-[:IN_CONTEXT]->context" +
            " WHERE version.ordinal >= {1} AND version.ordinal <= {2}" +
            " RETURN version" +
            " ORDER BY version.ordinal")
    Set<VersionDTO> findRange(InteractionContextDTO context, Long minOrdinal, Long maxOrdinal);

    @Query("START context=node({0})" +
            " MATCH version-[:IN_CONTEXT]->context" +
            " WHERE version.ordinal >= {1} AND version.ordinal <= {2} AND version.modelTypeFlag = {3} AND version.accessModeFlag <> 'X'" +
            " RETURN version" +
            " ORDER BY version.ordinal")
    Set<VersionDTO> findRange(InteractionContextDTO context, Long minOrdinal, Long maxOrdinal, char modelTypeFlag);

    @Query("START context=node({0})" +
            " MATCH version-[:IN_CONTEXT]->context" +
            " WHERE version.ordinal >= {1} AND version.ordinal <= {2} AND version.modelTypeFlag = {3} AND version.accessModeFlag = {4}" +
            " RETURN version" +
            " ORDER BY version.ordinal")
    Set<VersionDTO> findRange(InteractionContextDTO context, Long minOrdinal, Long maxOrdinal, char modelTypeFlag, char accessModeFlag);

}
