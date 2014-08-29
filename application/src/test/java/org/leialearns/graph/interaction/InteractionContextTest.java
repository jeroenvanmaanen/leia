package org.leialearns.graph.interaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml", "/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class})
public class InteractionContextTest {
    private final Logger logger = LoggerFactory.getLogger(InteractionContextTest.class);
    private static final String CONTEXT_URI = "http://leialearns/test";
    private static final String ACTIONS_URI = CONTEXT_URI + "/actions";

    @Autowired
    private AlphabetRepository alphabetRepository;

    @Autowired
    private InteractionContextRepository interactionContextRepository;

    @Autowired
    private InteractionContextDAO interactionContextDAO;

    @Test
    public void testDirect() {
        AlphabetDTO actions = new AlphabetDTO();
        actions.setURI(ACTIONS_URI);
        alphabetRepository.save(actions);

        InteractionContextDTO context = new InteractionContextDTO();
        context.setURI(CONTEXT_URI);
        context.setActions(actions);
        interactionContextRepository.save(context);

        logger.info("Actions: {}", context.getActions());

        context = interactionContextRepository.getInteractionContextByUri(CONTEXT_URI);
        logger.info("Actions: {}", context.getActions());

        interactionContextRepository.setEmptyVersionChain(context);

        logger.info("Actions: {}", context.getActions());
    }

    @Test
    public void testDAO() {
        InteractionContextDTO context = interactionContextDAO.findOrCreate(CONTEXT_URI);
        logger.info("Actions: {}", context.getActions());
    }

}