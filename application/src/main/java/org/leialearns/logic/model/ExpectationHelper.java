package org.leialearns.logic.model;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.logic.session.NeedsRoot;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.structure.Node;

public interface ExpectationHelper extends NeedsRoot {
    Expectation createExpectation(Root root);

    @BridgeOverride
    Expectation getExpectation(ExpectedModel expectedModel, Node node);

    @BridgeOverride
    Expectation getExpectation(Expected expected, Node node);

    @BridgeOverride
    Expectation getExpectation(Toggled toggled, Node node);

    void attach(Toggled toggled, Root root, Node node, Expectation expectation);
}
