package org.leialearns.api.model.common;

import org.leialearns.logic.model.Version;
import org.leialearns.api.structure.Node;

public interface NodeDataProxy<Type> {
    void setNode(Node node);
    Node getNode();
    void set(Version version, Node node);
    Version getVersion();
    void setData(Type data);
    Type getData();
}
