package org.leialearns.command.api;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.interaction.InteractionContext;
import org.leialearns.logic.model.Counted;
import org.leialearns.logic.model.Version;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.session.Session;
import org.leialearns.logic.structure.Structure;
import org.leialearns.utilities.ExecutionListener;
import org.leialearns.utilities.GraphDumper;
import org.leialearns.utilities.Setting;
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
import static org.junit.Assert.fail;
import static org.leialearns.utilities.Static.getLoggingClass;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml","/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class, ExecutionListener.class})
public class EncounterTest {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private static final Setting<String> PROJECT_DIR = new Setting<>("Project directory");
    private final Setting<String> interactionContextUri = new Setting<>("Interaction context URI");

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private Encounter encounter;

    @Autowired
    private Root root;

    @Autowired
    private GraphDumper graphDumper;

    @Autowired
    public void setInteractionContextUri(String interactionContextUri) {
        this.interactionContextUri.set(interactionContextUri);
    }

    public Session createSession() {
        return root.createSession(interactionContextUri.get());
    }

    @BeforeClass
    public static void beforeClass() throws IOException {
        TestUtilities.beforeClass(PROJECT_DIR);
    }

    @Test
    public void testEncounter() {
        logger.info("Start test");
        graphDumper.dumpGraph();
        try {
            transactionHelper.runInTransaction(new Runnable() {
                @Override
                public void run() {
//                    encounter.command("file://" + TestUtilities.getPath(PROJECT_DIR.get(), "pom.xml"));
                    String source = "file://" + TestUtilities.getPath(PROJECT_DIR.get(), "src/test/resources/data/test.txt");
                    try {
                        encounter.command(source);
                    } catch (Exception exception) {
                        logger.error(String.format("Exception while running encounter(%s)", source), exception);
                    }
                    Session session = createSession();
                    InteractionContext interactionContext = session.getInteractionContext();
                    for (Version version : interactionContext.getVersions()) {
                        logger.debug("Version: {}", version);
                    }
                    Structure structure = interactionContext.getStructure();
                    structure.logNodes();
                    Version version = session.findOrCreateLastVersion(ModelType.COUNTED, AccessMode.READABLE);
                    assertEquals(version, encounter.getLastVersion());
                    assertEquals(AccessMode.READABLE, version.getAccessMode());
                    Counted countedVersion = version.createCountedVersion();
                    countedVersion.logCounters();
                }
            });
        } catch (Throwable throwable) {
            logger.error("Exception in test", throwable);
            fail(throwable.toString());
        }
        graphDumper.dumpGraph();
    }

}
