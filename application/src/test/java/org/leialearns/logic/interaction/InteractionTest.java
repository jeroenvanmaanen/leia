package org.leialearns.logic.interaction;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.logic.session.Root;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
}
