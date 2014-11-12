package org.leialearns.logic.structure;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.api.enumerations.Direction;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.session.Root;
import org.leialearns.api.structure.Node;
import org.leialearns.api.structure.Structure;
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
public class StructureTest {
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
    public void testNodes() {
        logger.info("Start test");
        final boolean[] target = new boolean[] {false};
        try {
            transactionHelper.runInTransaction(() -> {
                String uri = "http://leialearns.org/test/nodes";
                InteractionContext interactionContext = TestUtilities.setupNodes(root, uri);
                assertNotNull(interactionContext);
                assertEquals(uri, interactionContext.getURI());

                Structure structure = interactionContext.getStructure();
                Node darkLeftNode = structure.findOrCreateNode(interactionContext.createPath(">left", "<dark"));
                assertNotNull("Dark left node", darkLeftNode);
                Symbol left = interactionContext.getActions().internalize("left");
                target[0] = true;
                darkLeftNode.findOrCreate(left, Direction.ACTION);
            });
            assertTrue("Expected IllegalStateException", false);
        } catch (IllegalStateException exception) {
            assertTrue("Test did not reach target before throwing exception", target[0]);
        }
    }

}
