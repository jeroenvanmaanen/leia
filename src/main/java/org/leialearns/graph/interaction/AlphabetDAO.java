package org.leialearns.graph.interaction;

import org.leialearns.graph.repositories.AlphabetRepository;
import org.leialearns.graph.repositories.SymbolRepository;
import org.leialearns.logic.interaction.Alphabet;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.bridge.Static.getFarObject;
import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.getLoggingClass;

public class AlphabetDAO {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    AlphabetRepository repository;

    @Autowired
    SymbolRepository symbolRepository;

    @Autowired
    SymbolDAO symbolDAO;

    public TypedIterable<AlphabetDTO> findAll() {
        return null; // TODO: implement
    }

    public AlphabetDTO find(String uri) {
        return null; // TODO: implement
    }

    public AlphabetDTO findOrCreate(String uri) {
        AlphabetDTO alphabet = repository.getAlphabetByUri(uri);
        if (alphabet == null) {
            alphabet = new AlphabetDTO();
            alphabet.setURI(uri);
            alphabet = repository.save(alphabet);
            repository.setEmptySymbolChain(alphabet);
            logger.debug("Chain count: " + repository.countWordChain(alphabet));
        }
        logger.debug("Alphabet: " + alphabet.toString());
        return alphabet;
    }

    public SymbolDTO internalize(AlphabetDTO alphabet, String denotation) {
        logger.trace(display(alphabet) + ".internalize(" + display(denotation) + ")");
        SymbolDTO symbol = repository.findSymbol(alphabet, denotation);
        if (symbol == null) {
            // Use getFixated rather than isFixated() to avoid forcing the state of the alphabet to fixated for subsequent operations
            if (alphabet.getFixated() == Boolean.TRUE) {
                // todo: Meager checking, should be synchronized and lock the alphabet record for writing.
                throw new IllegalStateException("Alphabet is fixated: [" + alphabet + "]");
            }
            symbol = new SymbolDTO();
            symbol.setAlphabet(alphabet);
            symbol.setDenotation(denotation);
            symbol = symbolRepository.save(symbol);
            symbolDAO.setOrdinal(symbol);
            logger.debug("Chain count: " + repository.countWordChain(alphabet));
            logger.debug("Internalized: " + symbol);
        } else {
            symbol.setAlphabet(alphabet);
            if (symbol.getOrdinal() == null) {
                symbolDAO.setOrdinal(symbol);
                symbolDAO.save(symbol);
            }
        }
        return symbol;
    }

    public void fixate(AlphabetDTO alphabetDTO) {
        // TODO: implement
    }

    public boolean equals(AlphabetDTO alphabet, Object other) {
        Object otherObject = (other instanceof Alphabet ? getFarObject((Alphabet) other, AlphabetDTO.class) : other);
        return alphabet.equals(otherObject);
    }

}
