package org.leialearns.logic.model;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.structure.Node;

public interface Locus {
    Version getVersion();
    Node getNode();
    Symbol getSymbol();
}
