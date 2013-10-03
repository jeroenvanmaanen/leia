package org.leialearns.graph.interaction;

import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.repositories.AlphabetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.getLoggingClass;

public class SymbolDAO extends IdDaoSupport<SymbolDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private AlphabetRepository alphabetRepository;

    public Long findLargestSymbolOrdinal(AlphabetDTO alphabet) {
        Long result = alphabetRepository.findLargestSymbolOrdinal(alphabet);
        return result < 0 ? null : result;
    }

    public SymbolDTO find(AlphabetDTO alphabet, String denotation) {
        return null; // TODO: implement
    }

    public void setOrdinal(SymbolDTO symbolDTO) {
        if (symbolDTO.getOrdinal() == null) {
            Long ordinal = alphabetRepository.getOrdinal(symbolDTO);
            logger.trace("Set ordinal of: [" + display(symbolDTO) + "]: to: " + ordinal);
            //symbolDTO.setOrdinal(ordinal);
        } else {
            logger.trace("Ordinal was already set: [" + symbolDTO + "]");
        }
    }

    public int compareTo(SymbolDTO thisSymbol, Object that) {
        return thisSymbol.compareTo((SymbolDTO) adapt(that, SymbolDTO.class));
    }

}
