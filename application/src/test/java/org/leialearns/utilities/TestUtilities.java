package org.leialearns.utilities;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.api.enumerations.Direction;
import org.leialearns.api.interaction.Alphabet;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.session.Root;
import org.leialearns.api.structure.Node;
import org.leialearns.api.structure.Structure;
import org.leialearns.common.Setting;
import org.leialearns.spring.test.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.leialearns.common.Display.display;
import static org.leialearns.common.Static.getLoggingClass;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml","/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class, ExecutionListener.class})
public class TestUtilities {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @BeforeClass
    public static void beforeClass() throws IOException {
        org.leialearns.spring.test.TestUtilities.beforeClass(null);
    }

    public static void beforeClass(Setting<String> projectDirSetting) throws IOException {
        org.leialearns.spring.test.TestUtilities.beforeClass(projectDirSetting);
    }

    @Test
    public void testDisplay() {
        logger.info(display(new String[] {"Hello", " ", "World", null}));
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
