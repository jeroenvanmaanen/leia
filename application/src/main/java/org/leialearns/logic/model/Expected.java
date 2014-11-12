package org.leialearns.logic.model;

import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.expectation.Estimate;
import org.leialearns.api.model.expectation.Fraction;
import org.leialearns.logic.structure.Node;

public interface Expected extends ExpectedModel {
    Toggled getToggled();
    void setToggled(Toggled toggled);

    @SuppressWarnings("unused")
    Estimate createEstimate(Node node, Symbol symbol, Fraction fraction);

    void copyEstimates(Version version);
    void logCounters();
    void logModel();
}
