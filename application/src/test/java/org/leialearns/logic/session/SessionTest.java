package org.leialearns.logic.session;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.logic.interaction.Alphabet;
import org.leialearns.logic.interaction.InteractionContext;
import org.leialearns.logic.interaction.Symbol;
import org.leialearns.utilities.ExecutionListener;
import org.leialearns.utilities.TestUtilities;
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
import static org.leialearns.utilities.Static.equal;
import static org.leialearns.utilities.Static.getLoggingClass;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml","/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class, ExecutionListener.class})
public class SessionTest {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private Root root;

    @BeforeClass
    public static void beforeClass() throws IOException {
        TestUtilities.beforeClass(null);
    }

    @Test
    public void testRoot() {
        InteractionContext interactionContext = root.createInteractionContext("http://leialearns.org/test");
        assertNotNull("Interaction context", interactionContext);
        assertNotNull("Actions", interactionContext.getActions());
        assertNotNull("Responses", interactionContext.getResponses());
        assertNotNull("Structure", interactionContext.getStructure());

        Alphabet.Iterable alphabetIterable = root.findAlphabets();
        assertFalse("Empty alphabetDAO.findAll()", alphabetIterable.isEmpty());
        for (Object object : alphabetIterable) {
            assertTrue("Expected alphabet: " + display(object), object instanceof Alphabet);
        }

        Alphabet actions = interactionContext.getActions();
        logger.debug("Actions: " + actions.toString());

        actions.internalize("");
        actions.internalize("left");
        Symbol right = actions.internalize("right");
        logger.debug("Symbol 'right': [" + right + "] in: [" + right.getAlphabet() + "]");

        Alphabet responses = interactionContext.getResponses();
        logger.debug("Responses: " + responses.toString());

        responses.internalize("dark");
        Symbol light = responses.internalize("light");
        logger.debug("Symbol 'light': [" + light + "] in: [" + light.getAlphabet() + "]");

        assertEquals("http://leialearns.org/test/structure", interactionContext.getStructure().getURI());
    }

    @Test
    public void testSessions() {
        Session rootSession = root.createSession("http://leialearns.org/test/sessions");
        InteractionContext interactionContext = root.createInteractionContext("http://leialearns.org/test/sessions");
        Session newSession = root.createSession(interactionContext);
        assertFalse("Sessions should be different: " + display(rootSession) + ": " + display(newSession), equal(rootSession, newSession));
        assertEquals(rootSession.getInteractionContext(), newSession.getInteractionContext());
    }

}
