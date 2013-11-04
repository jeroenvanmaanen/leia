package org.leialearns.graph.model;

import org.springframework.data.neo4j.repository.GraphRepository;

public interface ToggledRepository extends GraphRepository<ToggledDTO> {
    public ToggledDTO findByVersion(VersionDTO version);
}
