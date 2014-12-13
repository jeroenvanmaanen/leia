package org.leialearns.graph.common;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.aspects.core.NodeBacked;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static org.leialearns.common.Static.compare;
import static org.leialearns.common.Static.getLoggingClass;

public class GraphLogger {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    ExecutionEngine executionEngine;

    public void log(String label, Object object, @SuppressWarnings("unused") int radius) {
        Long nodeId;
        if (object instanceof NodeBacked) {
            nodeId = ((NodeBacked) object).getNodeId();
        } else if (object instanceof HasId) {
            nodeId = ((HasId) object).getId();
        } else {
            nodeId = null;
        }
        if (nodeId == null) {
            logger.debug("No node ID found for object: {}", object);
        } else {
            logger.debug("Found node ID for object: {}: {}", nodeId, object);
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("startNode", nodeId);
            final ExecutionResult result = executionEngine.execute("START n = node({startNode}) MATCH (n)-[r]-(m) RETURN n, r, m", parameters);
            Map<Node,Set<ArcTo>> arcs = new HashMap<>();
            Iterator<Map<String,Object>> it = result.iterator();
            Node startNode = null;
            while (it.hasNext()) {
                Map<String,Object> row = it.next();
                Node from = (Node) row.get("n");
                if (from.getId() == nodeId) {
                    startNode = from;
                }
                Relationship relationship = (Relationship) row.get("r");
                Node to = (Node) row.get("m");
                if (relationship != null && to != null) {
                    Set<ArcTo> nodeArcs;
                    if (arcs.containsKey(from)) {
                        nodeArcs = arcs.get(from);
                    } else {
                        nodeArcs = new HashSet<>();
                        arcs.put(from, nodeArcs);
                    }
                    nodeArcs.add(new ArcTo(relationship, to));
                } else {
                    logger.warn("Null? {}, {}", relationship, to);
                }
            }
            ArcTo top = new ArcTo(null, startNode);
            top.logNode = createLogNode(top);
            List<ArcTo> level = new ArrayList<>();
            level.add(top);
            while (!arcs.isEmpty()) {
                level = coat(level, arcs);
            }
            top.logNode.log(format("%s: ", label));
        }
    }

    protected List<ArcTo> coat(List<ArcTo> level, Map<Node,Set<ArcTo>> arcs) {
        List<ArcTo> nextLevel = new ArrayList<>();
        for (ArcTo arcTo : level) {
            Node node = arcTo.node;
            if (arcs.containsKey(node)) {
                for (ArcTo deeper : arcs.get(node)) {
                    deeper.logNode = createLogNode(deeper);
                    arcTo.logNode.add(deeper.logNode);
                    nextLevel.add(deeper);
                }
                arcs.remove(node);
            }
        }
        return nextLevel;
    }

    protected static LogNode<ArcTo> createLogNode(final ArcTo arcTo) {
        return new LogNode<>(arcTo, arcTo1 -> arcTo1.relationshipLabel + " " + arcTo1.nodeLabel);
    }

    protected static String formatRelationship(Relationship relationship, boolean reverse) {
        StringBuilder builder = new StringBuilder();
        if (relationship == null) {
            builder.append("-[?]-");
        } else {
            if (reverse) {
                builder.append('<');
            }
            builder.append("-[");
            builder.append(relationship.getId());
            builder.append(':');
            builder.append(relationship.getType().name());
            builder.append("]-");
            if (!reverse) {
                builder.append('>');
            }
        }
        return builder.toString();
    }

    protected static String formatNode(Node node) {
        StringBuilder builder = new StringBuilder();
        if (node == null) {
            builder.append("(?)");
        } else {
            builder.append('(');
            for (Label label : node.getLabels()) {
                builder.append(label.name());
                builder.append(": ");
            }
            boolean first = true;
            for (String propertyKey : node.getPropertyKeys()) {
                if (propertyKey.equals("facets")) {
                    continue;
                }
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(propertyKey);
                builder.append('=');
                builder.append(node.getProperty(propertyKey));
            }
            builder.append(" | #");
            builder.append(node.getId());
            builder.append(')');
        }
        return builder.toString();
    }

    protected static String getTypeName(Relationship relationship) {
        RelationshipType type = relationship == null ? null : relationship.getType();
        return type == null ? "" : type.name();
    }

    protected static class ArcTo implements Comparable<ArcTo> {
        private final Relationship relationship;
        private final Node node;
        private final String relationshipLabel;
        private final String nodeLabel;
        private LogNode logNode = null;
        private ArcTo(Relationship relationship, Node node) {
            this.relationship = relationship;
            this.node = node;
            if (relationship == null) {
                relationshipLabel = "-[/]-";
            } else {
                relationshipLabel = formatRelationship(relationship, relationship.getEndNode().getId() != node.getId());
            }
            nodeLabel = formatNode(node);
        }
        public int compareTo(@NotNull ArcTo other) {
            return compareToSafe(other);
        }
        private int compareToSafe(ArcTo other) {
            int result = other == null ? 1 : compare(getTypeName(relationship), getTypeName(other.relationship));
            if (result == 0) {
                result = nodeLabel.compareTo(other.nodeLabel);
            }
            if (result == 0) {
                result = Long.signum(relationship.getId() - other.relationship.getId());
            }
            return result;
        }
        public boolean equals(Object other) {
            boolean result;
            if (other instanceof ArcTo) {
                ArcTo otherArcTo = (ArcTo) other;
                result = relationship == null ? otherArcTo.relationship == null : relationship.equals(otherArcTo.relationship);
                if (result) {
                    result = node == null ? otherArcTo.node == null : node.equals(otherArcTo.node);
                }
            } else {
                result = false;
            }
            return result;
        }
        public int hashCode() {
            return (relationship == null ? 0 : relationship.hashCode()) + (node == null ? 0 : node.hashCode());
        }
    }
}
