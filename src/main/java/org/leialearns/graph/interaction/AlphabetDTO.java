package org.leialearns.graph.interaction;

import org.leialearns.bridge.FarObject;
import org.leialearns.bridge.GenericBridgeFacet;
import org.leialearns.graph.BaseGraphDTO;
import org.leialearns.logic.interaction.Alphabet;

import java.io.Serializable;

public class AlphabetDTO extends BaseGraphDTO implements Serializable, GenericBridgeFacet, Comparable<AlphabetDTO>, FarObject<Alphabet> {
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

    public Boolean getFixated() {
        return null; // TODO: implement
    }

    public boolean isFixated() {
        return false; // TODO: implement
    }

    public void markFixated() {
        // TODO: implement
    }

    public String toString() {
        return null; // TODO: implement
    }

    public int compareTo(AlphabetDTO alphabet) {
        return 0; // TODO: implement
    }

    public Alphabet declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
