package org.leialearns.graph;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.common.Display.display;
import static org.leialearns.common.Static.getLoggingClass;

public class GraphDumper {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private GraphDatabaseService graphDatabaseService;

    public void dumpGraph() {
        if (logger.isTraceEnabled()) {
            logger.trace("Begin graph dump");
            Iterable<org.neo4j.graphdb.Node> allNodes =
                    GlobalGraphOperations.at(graphDatabaseService).getAllNodes();
            for ( org.neo4j.graphdb.Node node : allNodes ) {
                Long nodeId = node.getId();
                logger.trace("Node: #{}", nodeId);
                for (String propertyKey : node.getPropertyKeys()) {
                    logger.trace("  Property: #{}: {}: {}", new Object[]{nodeId, propertyKey, display(node.getProperty(propertyKey))});
                }
                for (Relationship relationship : node.getRelationships()) {
                    Long relationshipId = relationship.getId();
                    logger.trace("  Relationship: #{}: #{} {} #{}", new Object[]{relationshipId, nodeId, arrow(relationship, node), relationship.getOtherNode(node).getId()});
                    for (String propertyKey : relationship.getPropertyKeys()) {
                        logger.trace("    Property: #{}: {}: {}", new Object[]{relationshipId, propertyKey, display(relationship.getProperty(propertyKey))});
                    }
                }
            }
            logger.trace("End graph dump");
        }
    }

    public String arrow(Relationship relationship, org.neo4j.graphdb.Node firstNode) {
        String result;
        String infix = "[:" + relationship.getType().name() + "]";
        if (relationship.getEndNode().getId() == firstNode.getId()) {
            result = "<-" + infix + '-';
        } else {
            result = "-" + infix + "->";
        }
        return result;
    }
}
