package org.leialearns.graph.interaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.utilities.ExecutionListener;
import org.leialearns.utilities.TransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml", "/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class,ExecutionListener.class})
public class InteractionContextTest {
    private final Logger logger = LoggerFactory.getLogger(InteractionContextTest.class);
    private static final String CONTEXT_URI = "http://leialearns/test";
    private static final String ACTIONS_URI = CONTEXT_URI + "/actions";

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private AlphabetDAO alphabetDAO;

    @Autowired
    private InteractionContextDAO interactionContextDAO;

    @Test
    public void testDirect() {
        transactionHelper.runInTransaction(new Runnable() {
            @Override
            public void run() {
                AlphabetDTO actions = new AlphabetDTO();
                actions.setURI(ACTIONS_URI);
                alphabetDAO.save(actions);

                InteractionContextDTO context = new InteractionContextDTO();
                context.setURI(CONTEXT_URI);
                context.setActions(actions);
                interactionContextDAO.save(context);

                logger.info("Actions: {}", context.getActions());

                context = interactionContextDAO.find(CONTEXT_URI);
                logger.info("Actions: {}", context.getActions());
            }
        });
    }

    @Test
    public void testDAO() {
        transactionHelper.runInTransaction(new Runnable() {
            @Override
            public void run() {
                InteractionContextDTO context = interactionContextDAO.findOrCreate(CONTEXT_URI);
                logger.info("Actions: {}", context.getActions());
            }
        });
    }

}
