package org.leialearns.graph.interaction;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface SymbolRepository extends GraphRepository<SymbolDTO> {
    SymbolDTO findById(Long id);

    @Query("START alphabet = node({0}) MATCH alphabet-[:HAS_WORD]->symbol WHERE symbol.ordinal = {1} RETURN symbol")
    SymbolDTO getSymbol(AlphabetDTO alphabet, Long ordinal);
}
