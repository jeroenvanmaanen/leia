package org.leialearns.api.model;

import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.expectation.Estimate;
import org.leialearns.api.model.expectation.Expectation;
import org.leialearns.api.model.expectation.Fraction;
import org.leialearns.api.structure.Node;
import org.leialearns.logic.session.Root;

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
