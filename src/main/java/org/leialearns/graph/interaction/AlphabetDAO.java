package org.leialearns.graph.interaction;

import org.leialearns.graph.KeyGraphNodeDAO;
import org.leialearns.utilities.TypedIterable;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.leialearns.utilities.Static.getLoggingClass;

public class AlphabetDAO extends KeyGraphNodeDAO<AlphabetDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    public AlphabetDAO() {
        super("Alphabet", "uri");
    }

    public TypedIterable<AlphabetDTO> findAll() {
        return null; // TODO: implement
    }

    public AlphabetDTO find(String uri) {
        return null; // TODO: implement
    }

    public AlphabetDTO findOrCreate(String uri) {
        Node alphabetNode = getOrCreate(uri);
        AlphabetDTO alphabetDTO = new AlphabetDTO();
        alphabetDTO.setGraphNode(alphabetNode);
        logger.debug("Alphabet: " + alphabetDTO.toString());
        return alphabetDTO;
    }

    public SymbolDTO internalize(AlphabetDTO alphabet, String denotation) {
        return null; // TODO: implement
    }

    public void fixate(AlphabetDTO alphabetDTO) {
        // TODO: implement
    }

    public boolean equals(AlphabetDTO alphabet, Object other) {
        return false; // TODO: implement
    }

}
