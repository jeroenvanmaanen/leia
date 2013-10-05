package org.leialearns.graph.repositories;

import org.leialearns.graph.interaction.AlphabetDTO;
import org.leialearns.graph.interaction.SymbolDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface AlphabetRepository extends GraphRepository<AlphabetDTO> {
    AlphabetDTO getAlphabetByUri(String uri);

    @Query("START alphabet=node({0}) CREATE UNIQUE alphabet-[new:NEXT_WORD]->alphabet RETURN true")
    boolean setEmptySymbolChain(AlphabetDTO alphabet);

    @Query("START alphabet=node({0}) MATCH alphabet-[:HAS_WORD]->symbol WHERE symbol.denotation = {1} RETURN symbol")
    SymbolDTO findSymbol(AlphabetDTO alphabet, String denotation);

    @Query("START symbol=node({0}) MATCH symbol<-[:HAS_WORD]-alphabet, before-[old:NEXT_WORD]->alphabet CREATE UNIQUE before-[:NEXT_WORD]->symbol-[:NEXT_WORD]->alphabet SET symbol.ordinal = coalesce(before.ordinal?, -1) + 1 DELETE old RETURN symbol.ordinal")
    Long getOrdinal(SymbolDTO symbol);

    @Query("START alphabet=node({0}) MATCH alphabet-[:NEXT_WORD*1..]->symbol-[:NEXT_WORD*1..]->alphabet RETURN count(filter(s in symbol: s.ordinal! >= 0))")
    Integer countWordChain(AlphabetDTO alphabet);

    @Query("START alphabet=node({0}) MATCH last-[:NEXT_WORD]->alphabet RETURN coalesce(last.ordinal?, -1)")
    Long findLargestSymbolOrdinal(AlphabetDTO alphabet);

    @Query("START alphabet=node RETURN alphabet")
    Set<AlphabetDTO> findAllAlphabets();

}
