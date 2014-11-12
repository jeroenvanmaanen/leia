package org.leialearns.api.model.common;

import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.Version;
import org.leialearns.api.structure.Node;

public interface Locus {
    Version getVersion();
    Node getNode();
    Symbol getSymbol();
}
