package org.leialearns.utilities;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.getLoggingClass;

public class GraphDumper {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private GraphDatabaseService graphDatabaseService;

    public void dumpGraph() {
        logger.info("Begin graph dump");
        Iterable<org.neo4j.graphdb.Node> allNodes =
                GlobalGraphOperations.at(graphDatabaseService).getAllNodes();
        for ( org.neo4j.graphdb.Node node : allNodes ) {
            Long nodeId = node.getId();
            logger.info("Node: #{}", nodeId);
            for (String propertyKey : node.getPropertyKeys()) {
                logger.info("  Property: #{}: {}: {}", new Object[]{nodeId, propertyKey, display(node.getProperty(propertyKey))});
            }
            for (Relationship relationship : node.getRelationships()) {
                Long relationshipId = relationship.getId();
                logger.info("  Relationship: #{}: #{} {} #{}", new Object[]{relationshipId, nodeId, arrow(relationship, node), relationship.getOtherNode(node).getId()});
                for (String propertyKey : relationship.getPropertyKeys()) {
                    logger.info("    Property: #{}: {}: {}", new Object[]{relationshipId, propertyKey, display(relationship.getProperty(propertyKey))});
                }
            }
        }
        logger.info("End graph dump");
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
