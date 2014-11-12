package org.leialearns.graph.interaction;

import org.leialearns.graph.IdDaoSupport;
import org.leialearns.api.interaction.Symbol;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.bridge.Static.getFarObject;

public class SymbolDAO extends IdDaoSupport<SymbolDTO> {

    @Autowired
    private SymbolRepository repository;

    @Override
    protected SymbolRepository getRepository() {
        return repository;
    }

    public int compareTo(SymbolDTO thisSymbol, Object that) {
        return thisSymbol.compareTo(adapt(that, SymbolDTO.class));
    }

    public boolean equals(SymbolDTO symbol, Object other) {
        Object otherObject = (other instanceof Symbol ? getFarObject((Symbol) other, SymbolDTO.class) : other);
        return symbol.equals(otherObject);
    }
}
