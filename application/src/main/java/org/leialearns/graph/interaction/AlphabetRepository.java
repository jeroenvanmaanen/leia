package org.leialearns.graph.interaction;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Set;

public interface AlphabetRepository extends GraphRepository<AlphabetDTO> {
    AlphabetDTO getAlphabetByUri(String uri);

    @Query("START alphabet=node({0}) MATCH alphabet-[:NEXT_WORD*1..]->symbol RETURN symbol")
    Set<SymbolDTO> findInternalizedSymbols(AlphabetDTO alphabet);

    @Query("START alphabet=node({0}) MATCH alphabet-[:HAS_WORD]->symbol WHERE symbol.denotation = {1} RETURN symbol")
    SymbolDTO findSymbol(AlphabetDTO alphabet, String denotation);

    @Query("START alphabet=node({0})" +
            " MATCH alphabet-[r:NEXT_WORD*0..]->()" +
            " WHERE length(r) = 0" +
            " CREATE UNIQUE alphabet-[:NEXT_WORD]->alphabet" +
            " RETURN length(r) = 0")
    boolean setEmptySymbolChain(AlphabetDTO alphabet);

    @Query("START alphabet=node({0}) RETURN alphabet.denotation?")
    String getDenotation(AlphabetDTO alphabet);

    @Query("START symbol=node({0})" +
            " MATCH symbol<-[:HAS_WORD]-alphabet, before-[old:NEXT_WORD]->alphabet" +
            " CREATE UNIQUE before-[:NEXT_WORD]->symbol-[:NEXT_WORD]->alphabet" +
            " SET symbol.ordinal = coalesce(before.ordinal?, -1) + 1" +
            " DELETE old" +
            " RETURN symbol.ordinal")
    Long getOrdinal(SymbolDTO symbol);

    @Query("START alphabet=node({0}) MATCH alphabet-[:NEXT_WORD*1..]->n RETURN count(n)")
    Integer countWordChain(AlphabetDTO alphabet);

    @Query("START alphabet=node({0}) MATCH last-[:NEXT_WORD]->alphabet RETURN coalesce(last.ordinal?, -1)")
    Long findLargestSymbolOrdinal(AlphabetDTO alphabet);

}
