package org.leialearns.logic.model;

import org.leialearns.api.model.Expected;
import org.leialearns.api.model.Toggled;
import org.leialearns.api.structure.Node;
import org.leialearns.bridge.BridgeOverride;

public class ExpectedHelper {

    @BridgeOverride
    public boolean isIncluded(Expected expected, Node node) {
        // todo: define self-contained implementation
        Toggled toggled = expected.getToggled();
        return toggled != null && toggled.isIncluded(node);
    }

}
