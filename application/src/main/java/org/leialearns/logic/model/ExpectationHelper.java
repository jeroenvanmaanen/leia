package org.leialearns.logic.model;

import org.leialearns.api.model.ExpectedModel;
import org.leialearns.api.model.Toggled;
import org.leialearns.api.model.expectation.Expectation;
import org.leialearns.api.session.Root;
import org.leialearns.api.structure.Node;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.logic.session.NeedsRoot;

public interface ExpectationHelper extends NeedsRoot {
    @BridgeOverride
    Expectation createExpectation(Root root);

    @BridgeOverride
    Expectation getExpectation(ExpectedModel expectedModel, Node node);

    @BridgeOverride
    Expectation getExpectation(Toggled toggled, Node node);

    @BridgeOverride
    void attach(Toggled toggled, Root root, Node node, Expectation expectation);
}
