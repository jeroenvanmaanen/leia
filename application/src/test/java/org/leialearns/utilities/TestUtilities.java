package org.leialearns.utilities;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.executable.LogConfigurator;
import org.leialearns.api.interaction.Alphabet;
import org.leialearns.enumerations.Direction;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.logic.session.Root;
import org.leialearns.api.structure.Node;
import org.leialearns.api.structure.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.getLoggingClass;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml","/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class, ExecutionListener.class})
public class TestUtilities {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @BeforeClass
    public static void beforeClass() throws IOException {
        beforeClass(null);
    }

    @Test
    public void testDisplay() {
        logger.info(display(new String[] {"Hello", " ", "World", null}));
    }

    public static void beforeClass(Setting<String> projectDirSetting) throws IOException {
        // Expected to be run from Maven, therefore the user.dir is assumed to be identical to the project directory.
        String projectDir = System.getProperty("user.dir");
        if (projectDirSetting != null) {
            projectDirSetting.set(projectDir);
        }
        System.err.print("Project directory: ");
        System.err.println(projectDir);
        String logDir = getPath(projectDir, "target", "log");
        String configDir = getPath(projectDir, "src", "test", "resources");
        String configFile = getPath(configDir, "logging.properties");
        if (!(new File(configFile).exists())) {
            configFile = getPath(configDir, "logging-sample+default.properties");
        }
        System.err.println("Configuring logging using config file: " + configFile);
        InputStream loggingProperties = new FileInputStream(configFile);
        new LogConfigurator(logDir).configure(loggingProperties);
    }

    public static String getPath(String... components) {
        File result = null;
        for (String component : components) {
            if (result == null) {
                result = new File(component);
            } else {
                result = new File(result, component);
            }
        }
        if (result == null) {
            throw new IllegalArgumentException("No path components given");
        }
        return result.getAbsolutePath();
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
