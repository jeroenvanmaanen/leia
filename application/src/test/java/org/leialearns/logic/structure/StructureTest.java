package org.leialearns.logic.structure;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.api.common.PrefixDecoder;
import org.leialearns.api.common.PrefixDecoderFactory;
import org.leialearns.api.common.PrefixEncoder;
import org.leialearns.api.common.PrefixEncoderFactory;
import org.leialearns.api.enumerations.Direction;
import org.leialearns.api.interaction.Alphabet;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.session.Root;
import org.leialearns.api.structure.Node;
import org.leialearns.api.structure.Structure;
import org.leialearns.utilities.ExceptionWrapper;
import org.leialearns.utilities.ExecutionListener;
import org.leialearns.utilities.Static;
import org.leialearns.utilities.TestUtilities;
import org.leialearns.utilities.TransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.leialearns.api.enumerations.Direction.ACTION;
import static org.leialearns.api.enumerations.Direction.RESPONSE;
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

    @Autowired
    PrefixEncoderFactory prefixEncoderFactory;

    @Autowired
    PrefixDecoderFactory prefixDecoderFactory;

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


    @Test
    public void testPrefixEncodeStructure() {
        transactionHelper.runInTransaction(() -> {
            try {
                prefixEncodeStructure();
            } catch (IOException exception) {
                throw ExceptionWrapper.wrap(exception);
            }
        });
    }

    private void prefixEncodeStructure() throws IOException {
        InteractionContext interactionContext = root.createInteractionContext("http://leialearns.org/test-structure-prefix-free");
        assertNotNull("Interaction context", interactionContext);
        assertNotNull("Actions", interactionContext.getActions());
        assertNotNull("Responses", interactionContext.getResponses());

        Alphabet actions = interactionContext.getActions();
        logger.debug("Actions: " + actions.toString());
        Symbol left = actions.internalize("left");
        Symbol right = actions.internalize("right");
        Alphabet responses = interactionContext.getResponses();
        logger.debug("Responses: " + actions.toString());
        Symbol dark = responses.internalize("dark");
        Symbol light = responses.internalize("light");

        Structure structure = interactionContext.getStructure();
        Node darkNode = structure.findOrCreateNode(dark, RESPONSE);
        structure.markExtensible(darkNode);
        Node leftDarkNode = darkNode.findOrCreate(left, ACTION);
        structure.markExtensible(leftDarkNode);
        leftDarkNode.findOrCreate(dark, RESPONSE);
        leftDarkNode.findOrCreate(light, RESPONSE);
        Node lightNode = structure.findOrCreateNode(light, RESPONSE);
        structure.markExtensible(lightNode);
        lightNode.findOrCreate(right, ACTION);

        StringWriter writer = new StringWriter();
        PrefixEncoder encoder = prefixEncoderFactory.createReadablePrefixEncoder(writer);
        structure.prefixEncode(encoder);
        encoder.close();

        String encoding = writer.toString();
        logger.debug("Encoding: <![CDATA[\n" + encoding + "]]>");

        // Setup new structure
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String timestamp = dateFormat.format(new Date());
        InteractionContext newInteractionContext = root.createInteractionContext("http://leialearns.org/test-structure-prefix-free-new/" + timestamp);
        copySymbols(actions, newInteractionContext.getActions());
        copySymbols(responses, newInteractionContext.getResponses());
        Structure newStructure = newInteractionContext.getStructure();

        StringReader reader = new StringReader(encoding);
        PrefixDecoder decoder = prefixDecoderFactory.createReadablePrefixDecoder(reader);
        decoder.addHelper(newInteractionContext, InteractionContext.class);
        newStructure.prefixDecode(decoder);

        assertEquals(2, Static.asList(newStructure.findRootNodes()).size());
    }

    private void copySymbols(Alphabet source, Alphabet target) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrefixEncoder encoder = prefixEncoderFactory.createBinaryPrefixEncoder(output);
        source.prefixEncode(encoder);
        encoder.close();

        ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        PrefixDecoder decoder = prefixDecoderFactory.createBinaryPrefixDecoder(input);
        target.prefixDecode(decoder);
    }
}
