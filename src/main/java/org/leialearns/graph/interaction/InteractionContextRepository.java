package org.leialearns.graph.interaction;

import org.springframework.data.neo4j.repository.GraphRepository;

public interface InteractionContextRepository extends GraphRepository<InteractionContextDTO> {
    InteractionContextDTO getInteractionContextByUri(String uri);
}
