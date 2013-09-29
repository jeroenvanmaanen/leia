package org.leialearns.graph.structure;

import org.leialearns.graph.interaction.DirectedSymbolDTO;
import org.leialearns.enumerations.Direction;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.repositories.StructureRepository;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.utilities.Static.getLoggingClass;

public class StructureDAO {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    StructureRepository repository;

    public StructureDTO find(String uri) {
        return null; // TODO: implement
    }

    public StructureDTO findOrCreate(String uri) {
        StructureDTO structureDTO = repository.getStructureByUri(uri);
        if (structureDTO == null) {
            structureDTO = new StructureDTO();
            structureDTO.setURI(uri);
            repository.save(structureDTO);
        }
        logger.debug("Structure: " + structureDTO);
        return structureDTO;
    }

    public void updateMaxDepth(final StructureDTO structure, NodeDTO node) {
        // TODO: implement
    }

    public NodeDTO findOrCreateNode(StructureDTO structure, TypedIterable<DirectedSymbolDTO> path) {
        return null; // TODO: implement
    }

    public NodeDTO findOrCreateNode(StructureDTO structure, SymbolDTO symbol, Direction direction) {
        return null; // TODO: implement
    }

    public NodeDTO findOrCreateNode(StructureDTO structureDTO, SymbolDTO action, TypedIterable<DirectedSymbolDTO> path) {
        return null; // TODO: implement
    }

    public NodeDTO findOrCreateNode(StructureDTO structureDTO, DirectedSymbolDTO firstSymbol, TypedIterable<DirectedSymbolDTO> path) {
        return null; // TODO: implement
    }

    public void logNodes(StructureDTO structure) {
        // TODO: implement
    }

    public boolean equals(StructureDTO structure, Object other) {
        return false; // TODO: implement
    }

}
