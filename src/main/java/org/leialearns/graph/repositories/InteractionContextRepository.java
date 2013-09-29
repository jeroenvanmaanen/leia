package org.leialearns.graph.repositories;

import org.leialearns.graph.interaction.InteractionContextDTO;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface InteractionContextRepository extends GraphRepository<InteractionContextDTO> {
    InteractionContextDTO getInteractionContextByUri(String uri);
}
