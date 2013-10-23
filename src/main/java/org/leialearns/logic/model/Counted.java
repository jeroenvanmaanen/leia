package org.leialearns.logic.model;

import org.leialearns.logic.interaction.DirectedSymbol;
import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.structure.Node;

public interface Counted extends TypedVersionExtension {
    Counter getCounter(Node node, Symbol symbol);
    Counter getCounter(DirectedSymbol.Iterable path, Symbol symbol);
    void logCounters();
}
