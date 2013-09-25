package org.leialearns.logic.model;

import org.leialearns.logic.session.Session;
import org.leialearns.logic.structure.Node;

public class ExpectedHelper {

    public boolean isIncluded(Expected expected, Node node, Session session) {
        // todo: define self-contained implementation
        Toggled toggled = expected.getToggled();
        return toggled != null && toggled.isIncluded(node, session);
    }

}
