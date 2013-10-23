package org.leialearns.logic.model;

import org.leialearns.logic.structure.Node;

public interface Distribution {
    void setNode(Node node);
    Node getNode();
    void set(Version version, Node node);
    Version getVersion();
    void log();
    void log(String label);
}
