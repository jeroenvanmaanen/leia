package org.leialearns.logic.model;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.api.enumerations.Direction;
import org.leialearns.api.enumerations.ModelType;
import org.leialearns.api.interaction.Alphabet;
import org.leialearns.api.interaction.DirectedSymbol;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.Counted;
import org.leialearns.api.model.Version;
import org.leialearns.api.model.histogram.Counter;
import org.leialearns.api.session.Root;
import org.leialearns.api.session.Session;
import org.leialearns.api.structure.Node;
import org.leialearns.api.structure.Structure;
import org.leialearns.spring.test.ExecutionListener;
import org.leialearns.spring.test.TestUtilities;
import org.leialearns.spring.test.TransactionHelper;
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
import static org.leialearns.common.Static.getLoggingClass;

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
        transactionHelper.runInTransaction(() -> {
            InteractionContext interactionContext = setupNodes(root, "http://leialearns.org/test/nodes");
            Session session = root.createSession(interactionContext);
            assertNotNull(session);

            Version version = session.findOrCreateLastVersion(ModelType.COUNTED, null);
            assertNotNull(version);
        });
    }

    @Test
    public void testCounters() {
        transactionHelper.runInTransaction(() -> {
            InteractionContext interactionContext = setupNodes(root, "http://leialearns.org/test/nodes");
            Session session = root.createSession(interactionContext);
            assertNotNull(session);
            Version version = session.findOrCreateLastVersion(ModelType.COUNTED, null);
            assertNotNull(version);
            DirectedSymbol.Iterable path = interactionContext.createPath(">left", "<dark");
            Structure structure = version.getInteractionContext().getStructure();
            Node node = structure.findOrCreateNode(path);
            assertNotNull(node);
            logger.debug("Node: [" + node + "]");
            InteractionContext updatedContext = root.createInteractionContext(interactionContext.getURI());
            assertTrue(updatedContext.getStructure().getMaxDepth() >= node.getDepth());

            Symbol light = interactionContext.getResponses().internalize("light");
            assertNotNull(light);

            Counted countedVersion = version.createCountedVersion();
            Counter counter = countedVersion.getCounter(path, light);
            assertNotNull(counter);
            long oldValue = counter.getValue();
            counter.increment();
            Counter updatedCounter = counter.fresh();
            assertEquals(oldValue + 1, updatedCounter.getValue());
        });
    }

    public static InteractionContext setupNodes(Root root, String interactionContextURI) {
        InteractionContext interactionContext = root.createInteractionContext(interactionContextURI);
        assertNotNull(interactionContext);
        Alphabet actions = interactionContext.getActions();
        Symbol left = actions.internalize("left");
        Symbol right = actions.internalize("right");
        Alphabet responses = interactionContext.getResponses();
        Symbol dark = responses.internalize("dark");
        responses.internalize("light");

        Structure structure = interactionContext.getStructure();
        assertNotNull("Structure", structure);
        Node leftNode = structure.findOrCreateNode(left, Direction.ACTION);
        assertNotNull("Left node", leftNode);
        Node rightNode = structure.findOrCreateNode(right, Direction.ACTION);
        assertNotNull("Right node", rightNode);

        structure.markExtensible(leftNode);
        Node darkLeftNode = leftNode.findOrCreate(dark, Direction.RESPONSE);
        assertNotNull(darkLeftNode);

        return interactionContext;
    }

}
