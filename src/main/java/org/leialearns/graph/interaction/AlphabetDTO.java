package org.leialearns.graph.interaction;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.HasId;
import org.leialearns.logic.interaction.Alphabet;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import java.io.Serializable;

import static org.leialearns.utilities.Display.displayParts;

@NodeEntity
public class AlphabetDTO extends BaseBridgeFacet implements HasId, Serializable, Comparable<AlphabetDTO>, FarObject<Alphabet> {
    @GraphId
    private Long id;

    @Indexed(unique = true, indexName = "alphabetUri")
    private String uri;

    private boolean fixated = false;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public Boolean getFixated() {
        return fixated;
    }

    public void markFixated() {
        fixated = true;
    }

    @Override
    public String toString() {
        return displayParts("Alphabet", id, fixated, uri);
    }

    @Override
    public int compareTo(AlphabetDTO alphabet) {
        return this.uri.compareTo(alphabet.getURI());
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AlphabetDTO && uri.equals(((AlphabetDTO) other).getURI());
    }

    @Override
    public int hashCode() {
        return uri == null ? 0 : uri.hashCode();
    }

    @Override
    public Alphabet declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
