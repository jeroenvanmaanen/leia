package org.leialearns.logic.interaction;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.api.common.PrefixDecoder;
import org.leialearns.api.common.PrefixDecoderFactory;
import org.leialearns.api.common.PrefixEncoder;
import org.leialearns.api.common.PrefixEncoderFactory;
import org.leialearns.api.interaction.Alphabet;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.session.Root;
import org.leialearns.utilities.ExceptionWrapper;
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
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.getLoggingClass;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml","/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class, ExecutionListener.class})
public class InteractionTest {
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
    public void testFixated() {
        transactionHelper.runInTransaction(() -> {
            InteractionContext interactionContext = root.createInteractionContext("http://leialearns.org/test-fixated");
            assertNotNull("Interaction context", interactionContext);
            assertNotNull("Actions", interactionContext.getActions());
            assertNotNull("Responses", interactionContext.getResponses());
            assertNotNull("Structure", interactionContext.getStructure());

            Alphabet actions = interactionContext.getActions();
            logger.debug("Actions: " + actions.toString());
            Symbol left = actions.internalize("left");
            Symbol right = actions.internalize("right");
            assertFalse(actions.isFixated());
            assertEquals(1, left.descriptionLength());
            assertEquals(3, right.descriptionLength());

            Alphabet responses = interactionContext.getResponses();
            logger.debug("Responses: " + responses.toString());
            Symbol dark = responses.internalize("dark");
            Symbol light = responses.internalize("light");
            Symbol dim = responses.internalize("dim");
            responses.fixate();
            logger.debug("Responses: " + display(responses) + ": " + System.identityHashCode(responses));
            Alphabet darkAlphabet = dark.getAlphabet();
            logger.debug("Dark alphabet: " + display(darkAlphabet) + ": " + System.identityHashCode(darkAlphabet));
            assertTrue("Responses.isFixated" + responses, responses.isFixated());
            assertTrue("Dark.alphabet.isFixated" + darkAlphabet, darkAlphabet.isFixated());
            long responseSymbolsDescriptionLength = responses.getFixatedDescriptionLength();
            logger.debug("Response symbols description length: " + responseSymbolsDescriptionLength);
            assertEquals(2, responseSymbolsDescriptionLength);
            assertEquals("Description length of 'dark'", responseSymbolsDescriptionLength, dark.descriptionLength());
            assertEquals("Description length of 'light'", responseSymbolsDescriptionLength, light.descriptionLength());
            assertEquals("Description length of 'dim'", responseSymbolsDescriptionLength, dim.descriptionLength());
        });
    }

    @Test
    public void testPrefixEncodeAlphabet() {
        transactionHelper.runInTransaction(() -> {
            try {
                prefixEncodeAlphabet();
            } catch (IOException exception) {
                throw ExceptionWrapper.wrap(exception);
            }
        });
    }

    private void prefixEncodeAlphabet() throws IOException {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String timestamp = dateFormat.format(new Date());
        InteractionContext interactionContext = root.createInteractionContext("http://leialearns.org/test-alphabet-prefix-free/" + timestamp);
        assertNotNull("Interaction context", interactionContext);
        assertNotNull("Actions", interactionContext.getActions());
        assertNotNull("Responses", interactionContext.getResponses());

        Alphabet actions = interactionContext.getActions();
        logger.debug("Actions: " + actions.toString());
        actions.internalize("left");
        actions.internalize("right");
        StringWriter writer = new StringWriter();
        PrefixEncoder encoder = prefixEncoderFactory.createReadablePrefixEncoder(writer);
        actions.prefixEncode(encoder);
        encoder.close();

        String encoding = writer.toString();
        logger.debug("Encoding: <![CDATA[\n" + encoding + "]]>");

        Alphabet responses = interactionContext.getResponses();
        assertNull(responses.findLargestSymbolOrdinal());
        StringReader reader = new StringReader(encoding);
        PrefixDecoder decoder = prefixDecoderFactory.createReadablePrefixDecoder(reader);
        responses.prefixDecode(decoder);
        Long largestSymbolOrdinal = responses.findLargestSymbolOrdinal();
        assertEquals(actions.findLargestSymbolOrdinal(), largestSymbolOrdinal);
        for (long i = 0; i <= largestSymbolOrdinal; i++) {
            Symbol symbol = responses.getSymbol(i);
            logger.debug("Decoded response symbol: {}", symbol);
            Symbol action = actions.getSymbol(i);
            assertEquals(action.getDenotation(), symbol.getDenotation());
        }
    }
}
