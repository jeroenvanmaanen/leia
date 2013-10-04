package org.leialearns.graph;

import org.leialearns.bridge.FarObject;
import org.leialearns.utilities.ExceptionWrapper;
import org.leialearns.utilities.Expression;
import org.leialearns.utilities.Setting;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.leialearns.bridge.Static.getFarObject;

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
        String prefix = label == null || label.isEmpty() ? "" : label + ":";
        return prefix + (object == null ? "?" : object.getId());
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
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    protected <FT extends FarObject<NT>, NT> FT adapt(Object object, Class<FT> type) {
        Class<NT> nearType = getNearClass(type);
        FT adapted;
        if (!type.isInstance(object) && nearType.isInstance(object)) {
            adapted = getFarObject(nearType.cast(object), type);
        } else {
            adapted = type.cast(object);
        }
        return adapted;
    }

    @SuppressWarnings("unchecked")
    protected <FT extends FarObject<NT>, NT> Class<NT> getNearClass(Class<FT> farClass) {
        Class<NT> nearType;
        try {
            Method typeGetter = farClass.getMethod("declareNearType");
            nearType = (Class<NT>) typeGetter.getReturnType();
        } catch (Exception exception) {
            throw ExceptionWrapper.wrap(exception);
        }
        return nearType;
    }

}
