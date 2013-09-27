package org.leialearns.graph;

import org.neo4j.graphdb.Node;

public interface HasId {
    public void setGraphNode(Node node);
    public Node getGraphNode();
}
