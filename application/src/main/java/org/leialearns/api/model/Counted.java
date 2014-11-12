package org.leialearns.api.model;

import org.leialearns.api.interaction.DirectedSymbol;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.histogram.Counter;
import org.leialearns.api.structure.Node;

public interface Counted extends TypedVersionExtension {
    Counter getCounter(Node node, Symbol symbol);
    Counter getCounter(DirectedSymbol.Iterable path, Symbol symbol);
    void logCounters();
}
