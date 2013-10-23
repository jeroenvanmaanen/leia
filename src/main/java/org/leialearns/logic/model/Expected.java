package org.leialearns.logic.model;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.structure.Node;

public interface Expected extends ExpectedModel {
    Toggled getToggled();
    void setToggled(Toggled toggled);
    Estimate createEstimate(Node node, Symbol symbol, Fraction fraction);
    void copyEstimates(Version version);
    void logCounters();
}
