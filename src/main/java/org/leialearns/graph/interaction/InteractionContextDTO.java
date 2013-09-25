package org.leialearns.graph.interaction;

import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.graph.structure.StructureDTO;
import org.leialearns.logic.interaction.InteractionContext;

public class InteractionContextDTO implements FarObject<InteractionContext>, HasId {

    public String getURI() {
        return null; // TODO: implement
    }

    public AlphabetDTO getActions() {
        return null; // TODO: implement
    }

    public AlphabetDTO getResponses() {
        return null; // TODO: implement
    }

    public StructureDTO getStructure() {
        return null; // TODO: implement
    }

    public int compareTo(InteractionContextDTO alphabet) {
        return -1; // TODO: implement
    }

    @Override
    public InteractionContext declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
