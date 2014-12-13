package org.leialearns.graph.interaction;

import org.leialearns.api.interaction.Alphabet;
import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.FarObject;
import org.leialearns.graph.common.HasId;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

import static org.leialearns.common.Display.displayParts;

//import org.springframework.data.neo4j.annotation.Indexed; // TODO: remove

@NodeEntity
@TypeAlias("Alphabet")
public class AlphabetDTO extends BaseBridgeFacet implements HasId, Serializable, Comparable<AlphabetDTO>, FarObject<Alphabet> {
    @GraphId
    private Long id;

    // @Indexed(unique = true) // TODO: remove
    private String uri;

    private boolean fixated = false;

    @RelatedTo(direction = Direction.OUTGOING, type = "HAS_SYMBOLS")
    private Set<SymbolDTO> symbols;

    @RelatedTo(direction = Direction.OUTGOING, type = "LAST_SYMBOL")
    private SymbolDTO lastSymbol;

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

    public Set<SymbolDTO> getSymbols() {
        return symbols;
    }

    public void setSymbols(Set<SymbolDTO> symbols) {
        this.symbols = symbols;
    }

    public SymbolDTO getLastSymbol() {
        return lastSymbol;
    }

    public void setLastSymbol(SymbolDTO lastSymbol) {
        this.lastSymbol = lastSymbol;
    }

    @Override
    public String toString() {
        return displayParts("Alphabet", id, fixated, uri);
    }

    @Override
    public int compareTo(@NotNull AlphabetDTO alphabet) {
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
