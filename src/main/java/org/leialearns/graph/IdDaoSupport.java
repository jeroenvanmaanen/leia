package org.leialearns.graph;

import org.leialearns.bridge.FarObject;
import org.leialearns.utilities.Expression;
import org.leialearns.utilities.Setting;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IdDaoSupport<DTO extends HasId & FarObject<?>> {

    @Autowired
    protected GraphDatabaseService graphDatabaseService;

    private Setting<ExecutionEngine> executionEngine = new Setting<ExecutionEngine>("Execution engine", new Expression<ExecutionEngine>() {
        @Override
        public ExecutionEngine get() {
            return new ExecutionEngine(graphDatabaseService);
        }
    });

    public static String toID(String label, HasId object) {
        return "???"; // TODO: implement
    }

    protected ExecutionEngine getExecutionEngine() {
        return executionEngine.get();
    }

    protected Relationship linkTo(Node sourceNode, String linkType, Node targetNode) {
        String cypher = "START source = node({sourceNodeId}),\n" +
                " target = node({targetNodeId})\n" +
                "CREATE UNIQUE source-[relation:" + linkType + "]->target\n" +
                "RETURN relation\n" +
                "";
        ExecutionEngine engine = new ExecutionEngine(graphDatabaseService);
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("sourceNodeId", sourceNode.getId());
        parameters.put("targetNodeId", targetNode.getId());
        ExecutionResult result;
        try {
            result = engine.execute(cypher, parameters);
        } catch (Exception exception) {
            throw new RuntimeException("Cypher query: [" + cypher + "]", exception);
        }
        Iterator<Relationship> it = result.columnAs("relation");
        return it.next();
    }

    public DTO save(DTO dto) {
        return dto; // TODO: implement
    }

    protected Object adapt(Object object) {
        return null; // TODO: implement
    }

}
