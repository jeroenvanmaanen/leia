package org.leialearns.logic.model;

import org.leialearns.api.interaction.Symbol;
import org.leialearns.logic.model.expectation.Estimate;
import org.leialearns.logic.model.expectation.Expectation;
import org.leialearns.logic.model.expectation.Fraction;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.structure.Node;

public interface Toggled extends ExpectedModel {
    Node getNode();
    void setNode(Node node);
    Boolean getInclude();

    @SuppressWarnings("unused")
    void setInclude(Boolean include);

    Expected getExpected();
    void setExpected(Expected expected);
    void attach(Root root, Node node, Expectation expectation);
    Estimate createEstimate(Node node, Symbol symbol, Fraction fraction);
}
