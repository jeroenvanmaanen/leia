package org.leialearns.graph.interaction;

import org.leialearns.graph.IdDaoSupport;

public class SymbolDAO extends IdDaoSupport<SymbolDTO> {

    public Long findLargestSymbolOrdinal(AlphabetDTO alphabetDTO) {
        return null; // TODO: implement
    }

    public int compareTo(SymbolDTO thisSymbol, Object that) {
        return thisSymbol.compareTo((SymbolDTO) adapt(that));
    }

}
