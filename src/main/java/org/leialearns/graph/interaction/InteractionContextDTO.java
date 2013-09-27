package org.leialearns.graph.interaction;

import org.leialearns.bridge.FarObject;
import org.leialearns.graph.BaseGraphDTO;
import org.leialearns.logic.interaction.InteractionContext;
import org.leialearns.graph.structure.StructureDTO;
import java.io.Serializable;

public class InteractionContextDTO extends BaseGraphDTO implements Serializable, Comparable<InteractionContextDTO>, FarObject<InteractionContext> {
    private AlphabetDTO actions;
    private AlphabetDTO responses;

    public Long getId() {
        return null; // TODO: implement
    }

    public void setId(Long id) {
        // TODO: implement
    }

    public String getURI() {
        return null; // TODO: implement
    }

    public void setURI(String uri) {
        // TODO: implement
    }

    public AlphabetDTO getActions() {
        return actions;
    }

    public void setActions(AlphabetDTO actions) {
        this.actions = actions;
    }

    public AlphabetDTO getResponses() {
        return responses;
    }

    public void setResponses(AlphabetDTO responses) {
        this.responses = responses;
    }

    public StructureDTO getStructure() {
        return null; // TODO: implement
    }

    public void setStructure(StructureDTO structure) {
        // TODO: implement
    }

    public int compareTo(InteractionContextDTO interactionContext) {
        return 0; // TODO: implement
    }

    public boolean equals(Object other) {
        return false; // TODO: implement
    }

    public String toString() {
        return null; // TODO: implement
    }

    public InteractionContext declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
