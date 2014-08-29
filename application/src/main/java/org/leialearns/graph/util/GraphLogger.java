package org.leialearns.graph.util;

import org.leialearns.graph.HasId;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.aspects.core.NodeBacked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static org.leialearns.utilities.Static.getLoggingClass;

public class GraphLogger {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    ExecutionEngine executionEngine;

    public void log(String label, Object object, int radius) {
        Long nodeId;
        if (object instanceof NodeBacked) {
            nodeId = ((NodeBacked) object).getNodeId();
        } else if (object instanceof HasId) {
            nodeId = ((HasId) object).getId();
        } else {
            nodeId = null;
        }
        if (nodeId != null) {
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("startNode", nodeId);
            final ExecutionResult result = executionEngine.execute("START n = node({startNode}) MATCH (n)-[r]->(m) RETURN n, r, m", parameters);
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
                Set<ArcTo> nodeArcs;
                if (arcs.containsKey(from)) {
                    nodeArcs = arcs.get(from);
                } else {
                    nodeArcs = new HashSet<>();
                    arcs.put(from, nodeArcs);
                }
                nodeArcs.add(new ArcTo(relationship, to));
            }
            ArcTo top = new ArcTo(null, startNode);
            top.logNode = new LogNode(formatNode(startNode));
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
                    boolean reverse = deeper.relationship.getEndNode().getId() != deeper.node.getId();
                    String formattedRelationship = formatRelationship(deeper.relationship, reverse);
                    String formattedNode = formatNode(deeper.node);
                    deeper.logNode = new LogNode(format("%s %s", formattedRelationship, formattedNode));
                    arcTo.logNode.add(deeper.logNode);
                    nextLevel.add(deeper);
                }
                arcs.remove(node);
            }
        }
        return nextLevel;
    }

    protected String formatRelationship(Relationship relationship, boolean reverse) {
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

    protected String formatNode(Node node) {
        StringBuilder builder = new StringBuilder();
        if (node == null) {
            builder.append("(?)");
        } else {
            builder.append('(');
            builder.append(node.getId());
            builder.append(':');
            for (Label label : node.getLabels()) {
                builder.append(label.name());
                builder.append(':');
            }
            for (String propertyKey : node.getPropertyKeys()) {
                builder.append(propertyKey);
                builder.append('=');
                builder.append(node.getProperty(propertyKey));
                builder.append(", ");
            }
            builder.append(')');
        }
        return builder.toString();
    }

    protected static class ArcTo {
        private final Relationship relationship;
        private final Node node;
        private LogNode logNode = null;
        private ArcTo(Relationship relationship, Node node) {
            this.relationship = relationship;
            this.node = node;
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