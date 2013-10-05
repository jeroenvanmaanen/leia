package org.leialearns.graph.repositories;

import org.leialearns.graph.session.SessionDTO;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface SessionRepository extends GraphRepository<SessionDTO> {
}
