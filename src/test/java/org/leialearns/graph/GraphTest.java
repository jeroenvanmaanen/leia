package org.leialearns.graph;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.utilities.ExecutionListener;
import org.leialearns.utilities.Expression;
import org.leialearns.utilities.Setting;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.leialearns.utilities.Static.getLoggingClass;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class, ExecutionListener.class})
public class GraphTest {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private GraphDatabaseService graphDatabaseService;

    private Setting<ExecutionEngine> executionEngine = new Setting<ExecutionEngine>("Execution engine", new Expression<ExecutionEngine>() {
        @Override
        public ExecutionEngine get() {
            logger.info("Graph database service: [" + String.valueOf(graphDatabaseService) + "]");
            createDatabase(graphDatabaseService);
            return new ExecutionEngine(graphDatabaseService);
        }
    });

    @Test
    public void testGraph() {
        // when
        ExecutionResult results = distance("Ben", "Mike");
        // then
        assertTrue( results.iterator().hasNext() );
        assertEquals( 4, results.iterator().next().get( "distance" ) );
    }

    public ExecutionResult distance( String firstUser, String secondUser )
    {
        String query = "START first=node:user({firstUserQuery}),\n" +
                " second=node:user({secondUserQuery})\n" +
                "MATCH p=shortestPath(first-[*..4]-second)\n" +
                "RETURN length(p) AS distance";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "firstUserQuery", "name:" + firstUser );
        params.put( "secondUserQuery", "name:" + secondUser );
        return executionEngine.get().execute( query, params );
    }

    public static GraphDatabaseService createDatabase(GraphDatabaseService db) {
        // Create nodes
        String cypher = "CREATE\n" +
                "(ben {name:'Ben', _label:'user'}),\n" +
                "(arnold {name:'Arnold', _label:'user'}),\n" +
                "(charlie {name:'Charlie', _label:'user'}),\n" +
                "(gordon {name:'Gordon', _label:'user'}),\n" +
                "(lucy {name:'Lucy', _label:'user'}),\n" +
                "(emily {name:'Emily', _label:'user'}),\n" +
                "(sarah {name:'Sarah', _label:'user'}),\n" +
                "(kate {name:'Kate', _label:'user'}),\n" +
                "(mike {name:'Mike', _label:'user'}),\n" +
                "(paula {name:'Paula', _label:'user'}),\n" +
                "ben-[:FRIEND]->charlie,\n" +
                "charlie-[:FRIEND]->lucy,\n" +
                "lucy-[:FRIEND]->sarah,\n" +
                "sarah-[:FRIEND]->mike,\n" +
                "arnold-[:FRIEND]->gordon,\n" +
                "gordon-[:FRIEND]->emily,\n" +
                "emily-[:FRIEND]->kate,\n" +
                "kate-[:FRIEND]->paula";
        ExecutionEngine engine = new ExecutionEngine( db );
        engine.execute( cypher );

        // Index all nodes in "user" index
        Transaction tx = db.beginTx();
        try {
            Iterable<Node> allNodes =
                    GlobalGraphOperations.at(db).getAllNodes();
            for ( Node node : allNodes ) {
                if ( node.hasProperty( "name" ) )
                {
                    db.index().forNodes( "user" )
                            .add( node, "name", node.getProperty( "name" ) );
                }
            }
            tx.success();
        } finally {
            tx.finish();
        }
        return db;
    }
}
