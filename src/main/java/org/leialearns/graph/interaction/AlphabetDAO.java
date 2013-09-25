package org.leialearns.graph.interaction;

import org.leialearns.graph.IdDaoSupport;

public class AlphabetDAO extends IdDaoSupport<AlphabetDTO> {

    public SymbolDTO internalize(AlphabetDTO alphabet, String denotation) {
        return null; // TODO: implement
    }

    public void fixate(AlphabetDTO alphabetDTO) {
        // TODO: implement
    }

    public int compareTo(AlphabetDTO thisAlphabet, Object that) {
        return thisAlphabet.compareTo((AlphabetDTO) adapt(that));
    }

}
