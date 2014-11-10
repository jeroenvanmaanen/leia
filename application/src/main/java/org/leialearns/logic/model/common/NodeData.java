package org.leialearns.logic.model.common;

import org.leialearns.logic.model.Version;
import org.leialearns.logic.structure.Node;

import java.util.function.Supplier;

public interface NodeData<Type,ItemIterable> {
    void setNode(Node node);
    Node getNode();
    void set(Version version, Node node);
    Version getVersion();
    void log();
    void log(String label);
    void setData(Type data);
    Type getData();
    void retrieve(Supplier<ItemIterable> getItems);
}
