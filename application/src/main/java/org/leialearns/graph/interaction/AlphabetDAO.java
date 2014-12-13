package org.leialearns.graph.interaction;

import org.leialearns.api.interaction.Alphabet;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.common.TypedIterable;
import org.leialearns.graph.common.IdDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.leialearns.bridge.Static.getFarObject;
import static org.leialearns.common.Display.display;
import static org.leialearns.common.Static.getLoggingClass;

public class AlphabetDAO extends IdDaoSupport<AlphabetDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private Map<Long,Boolean> isFixated = new HashMap<>();

    @Autowired
    AlphabetRepository repository;

    @Autowired
    SymbolRepository symbolRepository;

    @Autowired
    SymbolDAO symbolDAO;

    @Override
    protected AlphabetRepository getRepository() {
        return repository;
    }

    public TypedIterable<AlphabetDTO> findAll() {
        return new TypedIterable<>(repository.findAll(), AlphabetDTO.class);
    }

    public AlphabetDTO find(String uri) {
        AlphabetDTO result = repository.getAlphabetByUri(uri);
        logger.debug("Find: {}: {}", uri, result);
        return result;
    }

    public AlphabetDTO findOrCreate(String uri) {
        AlphabetDTO alphabet = find(uri);
        if (alphabet == null) {
            alphabet = new AlphabetDTO();
            alphabet.setURI(uri);
            logger.debug("Alphabet: " + alphabet.toString());
            alphabet = repository.save(alphabet);
            if (logger.isDebugEnabled()) {
                logger.debug("Alphabet: " + alphabet.toString());
            }
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
        }

        symbol.setAlphabet(alphabet);
        if (symbol.getOrdinal() == null) {
            setOrdinal(symbol, alphabet);
        }

        return symbol;
    }

    protected void setOrdinal(SymbolDTO symbol, AlphabetDTO alphabet) {
        SymbolDTO lastSymbol = alphabet.getLastSymbol();
        Long lastOrdinal = lastSymbol == null ? null : lastSymbol.getOrdinal();
        long nextOrdinal = lastOrdinal == null ? 0L : lastOrdinal + 1L;
        symbol.setOrdinal(nextOrdinal);
        symbol = symbolRepository.save(symbol);

        if (lastSymbol != null) {
            lastSymbol.setNextSymbol(symbol);
            symbolDAO.save(lastSymbol);
        }


        alphabet.setLastSymbol(symbol);
        save(alphabet);

        logger.debug("Internalized: " + symbol);
    }

    @BridgeOverride
    public Long findLargestSymbolOrdinal(AlphabetDTO alphabet) {
        return repository.findLargestSymbolOrdinal(alphabet);
    }

    @BridgeOverride
    public void fixate(AlphabetDTO alphabetDTO) {
        alphabetDTO.markFixated();
        repository.save(alphabetDTO);
        logger.debug("Fixated alphabet: [" + alphabetDTO + "]");
    }

    @BridgeOverride
    public boolean isFixated(AlphabetDTO alphabet) {
        Long alphabetId = alphabet.getId();
        boolean result;
        if (isFixated.containsKey(alphabetId)) {
            result = isFixated.get(alphabetId);
        } else {
            result = alphabet.getFixated();
            isFixated.put(alphabetId, result);
        }
        return result;
    }

    public boolean equals(AlphabetDTO alphabet, Object other) {
        Object otherObject = (other instanceof Alphabet ? getFarObject((Alphabet) other, AlphabetDTO.class) : other);
        return alphabet.equals(otherObject);
    }

}
