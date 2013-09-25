package org.leialearns.logic.model;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.structure.Node;

public interface Toggled extends ExpectedModel {
    Node getNode();
    void setNode(Node node);
    Boolean getInclude();
    void setInclude(Boolean include);
    Expected getExpected();
    void setExpected(Expected expected);
    void attach(Root root, Node node, Expectation expectation);
    Estimate createEstimate(Node node, Symbol symbol, Fraction fraction);
}
