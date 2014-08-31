package org.leialearns.graph.interaction;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface AlphabetRepository extends GraphRepository<AlphabetDTO> {
    AlphabetDTO getAlphabetByUri(String uri);

    @Query("START alphabet=node({0}) MATCH alphabet-[:HAS_WORD]->symbol WHERE symbol.denotation = {1} RETURN symbol")
    SymbolDTO findSymbol(AlphabetDTO alphabet, String denotation);

    @Query("START alphabet=node({0}) MATCH alphabet-[:LAST_SYMBOL]->last RETURN last.ordinal")
    Long findLargestSymbolOrdinal(AlphabetDTO alphabetDTO);
}
