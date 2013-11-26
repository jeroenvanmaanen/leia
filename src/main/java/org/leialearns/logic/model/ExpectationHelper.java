package org.leialearns.logic.model;

import org.leialearns.logic.session.NeedsRoot;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.structure.Node;

public interface ExpectationHelper extends NeedsRoot {
    Expectation createExpectation(Root root);
    Expectation getExpectation(ExpectedModel expectedModel, Node node);
    Expectation getExpectation(Expected expected, Node node);
    Expectation getExpectation(Toggled toggled, Node node);
    void attach(Toggled toggled, Root root, Node node, Expectation expectation);
}
