package org.leialearns.logic.model;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.logic.session.Session;
import org.leialearns.logic.structure.Node;

public class ExpectedHelper {

    @BridgeOverride
    public boolean isIncluded(Expected expected, Node node) {
        // todo: define self-contained implementation
        Toggled toggled = expected.getToggled();
        return toggled != null && toggled.isIncluded(node);
    }

}
