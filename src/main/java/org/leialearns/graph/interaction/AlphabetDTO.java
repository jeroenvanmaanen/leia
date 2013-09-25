package org.leialearns.graph.interaction;

import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.logic.interaction.Alphabet;

public class AlphabetDTO implements FarObject<Alphabet>, HasId {

    public boolean isFixated() {
        return false; // TODO: implement
    }

    public String getURI() {
        return null; // TODO: implement
    }

    public int compareTo(AlphabetDTO alphabet) {
        return -1; // TODO: implement
    }

    @Override
    public Alphabet declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
