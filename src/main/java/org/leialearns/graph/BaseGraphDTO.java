package org.leialearns.graph;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.utilities.Setting;
import org.neo4j.graphdb.Node;

public class BaseGraphDTO extends BaseBridgeFacet implements HasId {
    private Setting<Node> node = new Setting<Node>("Node");

    public void setGraphNode(Node node) {
        this.node.set(node);
    }

    public Node getGraphNode() {
        return node.get();
    }

}
