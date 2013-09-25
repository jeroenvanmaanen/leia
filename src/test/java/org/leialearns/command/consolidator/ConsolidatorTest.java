package org.leialearns.command.consolidator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.session.Session;
import org.leialearns.utilities.ExecutionListener;
import org.leialearns.utilities.Expression;
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

import static org.leialearns.utilities.Static.getLoggingClass;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml","/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class, ExecutionListener.class})
public class ConsolidatorTest {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Setting<String> interactionContextUri = new Setting<String>("Interaction context URI");
    private final Setting<Session> session = new Setting<Session>("Session", new Expression<Session>() {
        @Override
        public Session get() {
            return createSession();
        }
    });

    @Autowired
    private Consolidator consolidator;

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private Root root;

    @Autowired
    public void setInteractionContextUri(String interactionContextUri) {
        this.interactionContextUri.set(interactionContextUri);
    }

    public Session createSession() {
        return root.createSession(interactionContextUri.get());
    }

    public Session getSession() {
        return session.get();
    }

    @BeforeClass
    public static void beforeClass() throws IOException {
        TestUtilities.beforeClass(null);
    }

    @Test
    public void testConsolidator() {
        transactionHelper.runInTransaction(new Runnable() {
            @Override
            public void run() {
                consolidator.command();
                logger.info("Consolidator finished: " + consolidator.getLastExpected());
            }
        });
    }

}
