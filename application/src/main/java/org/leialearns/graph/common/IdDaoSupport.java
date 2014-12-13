package org.leialearns.graph.common;

import org.leialearns.bridge.FarObject;
import org.leialearns.common.ExceptionWrapper;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
// import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.leialearns.bridge.Static.getFarObject;

public class IdDaoSupport<DTO extends HasId & FarObject<?>> {

    @Autowired
    private ExecutionEngine executionEngine;

    public static String toID(String label, HasId object) {
        String prefix = label == null || label.isEmpty() ? "" : label + ":";
        return prefix + (object == null ? "?" : object.getId());
    }

    protected ExecutionEngine getExecutionEngine() {
        return executionEngine;
    }

    protected Relationship linkTo(HasId sourceNode, String linkType, HasId targetNode) {
        return linkTo(sourceNode.getId(), linkType, targetNode.getId());
    }

    /*
    protected Relationship linkTo(Node sourceNode, String linkType, Node targetNode) {
        return linkTo(sourceNode.getId(), linkType, targetNode.getId());
    }
    */

    protected Relationship linkTo(Long sourceNodeId, String linkType, Long targetNodeId) {
        String cypher =
                "START source = node({sourceNodeId})," +
                "      target = node({targetNodeId})" +
                " CREATE UNIQUE source-[relation:" + linkType + "]->target" +
                " RETURN relation";
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("sourceNodeId", sourceNodeId);
        parameters.put("targetNodeId", targetNodeId);
        ExecutionResult result;
        try {
            result = getExecutionEngine().execute(cypher, parameters);
        } catch (Exception exception) {
            throw new RuntimeException("Cypher query: [" + cypher + "]", exception);
        }
        Iterator<Relationship> it = result.columnAs("relation");
        return it.next();
    }

    protected GraphRepository<DTO> getRepository() {
        throw new UnsupportedOperationException(String.format("DAO does not override method getRepository(): %s", getClass().getSimpleName()));
    }

    public DTO save(DTO dto) {
        return getRepository().save(dto);
    }

    /**
     * Executes a Neo4J query with the given parameters and casts the result to a DTO.
     * @param query The query to execute
     * @param parameters The parameters
     * @return The result of the query as a DTO
     */
    protected DTO findDTO(String query, Object... parameters) {
        return getRepository().query(query, createParameterMap(parameters)).singleOrNull();
    }

    /*
    protected <T>  T findFirst(Class<T> type, String query, Object... parameters) {
        ExecutionResult result = getExecutionEngine().execute(query, createParameterMap(parameters));
        String column = result.columns().iterator().next();
        return type.cast(result.columnAs(column).next());
    }
    */

    protected Map<String, Object> createParameterMap(Object[] parameters) {
        Map<String,Object> parameterMap = new HashMap<>();
        int n = 0;
        for (Object parameter : parameters) {
            n++;
            parameterMap.put("p" + n, parameter);
        }
        return parameterMap;
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
