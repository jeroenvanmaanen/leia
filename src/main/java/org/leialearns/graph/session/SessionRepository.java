package org.leialearns.graph.session;

import org.springframework.data.neo4j.repository.GraphRepository;

public interface SessionRepository extends GraphRepository<SessionDTO> {
}
