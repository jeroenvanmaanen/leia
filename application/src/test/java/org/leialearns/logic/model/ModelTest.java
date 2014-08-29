package org.leialearns.logic.model;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.interaction.DirectedSymbol;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.interaction.InteractionContext;
import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.session.Session;
import org.leialearns.logic.structure.Node;
import org.leialearns.logic.structure.Structure;
import org.leialearns.utilities.ExecutionListener;
import org.leialearns.utilities.TestUtilities;
import org.leialearns.utilities.TransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.leialearns.utilities.Static.getLoggingClass;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml","/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class, ExecutionListener.class})
public class ModelTest {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private Root root;

    @BeforeClass
    public static void beforeClass() throws IOException {
        TestUtilities.beforeClass(null);
    }

    @Test
    public void testVersions() {
        logger.info("Start test");
        transactionHelper.runInTransaction(new Runnable() {
            @Override
            public void run() {
                InteractionContext interactionContext = TestUtilities.setupNodes(root, "http://leialearns.org/test/nodes");
                Session session = root.createSession(interactionContext);
                assertNotNull(session);

                Version version = session.findOrCreateLastVersion(ModelType.COUNTED, null);
                assertNotNull(version);
            }
        });
    }

    @Test
    public void testCounters() {
        logger.info("Start test");
        transactionHelper.runInTransaction(new Runnable() {
            @Override
            public void run() {
                InteractionContext interactionContext = TestUtilities.setupNodes(root, "http://leialearns.org/test/nodes");
                Session session = root.createSession(interactionContext);
                assertNotNull(session);
                Version version = session.findOrCreateLastVersion(ModelType.COUNTED, null);
                assertNotNull(version);
                DirectedSymbol.Iterable path = interactionContext.createPath(">left", "<dark");
                Structure structure = version.getInteractionContext().getStructure();
                Node node = structure.findOrCreateNode(path);
                assertNotNull(node);
                logger.debug("Node: [" + node + "]");
                assertTrue(node.getStructure().getMaxDepth() >= node.getDepth());

                Symbol light = interactionContext.getResponses().internalize("light");
                assertNotNull(light);

                Counted countedVersion = version.createCountedVersion();
                Counter counter = countedVersion.getCounter(path, light);
                assertNotNull(counter);
                long oldValue = counter.getValue();
                counter.increment();
                assertEquals(oldValue + 1, counter.getValue());
            }
        });
    }

}