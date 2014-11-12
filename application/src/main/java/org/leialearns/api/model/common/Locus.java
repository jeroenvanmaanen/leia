package org.leialearns.api.model.common;

import org.leialearns.api.interaction.Symbol;
import org.leialearns.logic.model.Version;
import org.leialearns.logic.structure.Node;

public interface Locus {
    Version getVersion();
    Node getNode();
    Symbol getSymbol();
}
