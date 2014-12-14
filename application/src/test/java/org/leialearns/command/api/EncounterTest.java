package org.leialearns.command.api;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.api.command.Encounter;
import org.leialearns.api.enumerations.AccessMode;
import org.leialearns.api.enumerations.ModelType;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.api.model.Counted;
import org.leialearns.api.model.Version;
import org.leialearns.api.session.Root;
import org.leialearns.api.session.Session;
import org.leialearns.api.structure.Structure;
import org.leialearns.common.Setting;
import org.leialearns.spring.test.ExecutionListener;
import org.leialearns.spring.test.TestUtilities;
import org.leialearns.spring.test.TransactionHelper;
import org.leialearns.graph.GraphDumper;
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
import static org.leialearns.common.Static.getLoggingClass;

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
        graphDumper.dumpGraph();
        try {
            transactionHelper.runInTransaction(() -> {
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
            });
        } catch (Throwable throwable) {
            logger.error("Exception in test", throwable);
            fail(throwable.toString());
        }
        graphDumper.dumpGraph();
    }

}
