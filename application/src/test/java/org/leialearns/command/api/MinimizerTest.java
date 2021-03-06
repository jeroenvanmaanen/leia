package org.leialearns.command.api;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.api.command.Minimizer;
import org.leialearns.api.enumerations.AccessMode;
import org.leialearns.api.enumerations.ModelType;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.Observed;
import org.leialearns.api.model.Version;
import org.leialearns.api.model.expectation.Expectation;
import org.leialearns.api.model.expectation.Fraction;
import org.leialearns.api.model.histogram.Counter;
import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.api.session.Root;
import org.leialearns.api.session.Session;
import org.leialearns.api.structure.Node;
import org.leialearns.api.structure.Structure;
import org.leialearns.common.Setting;
import org.leialearns.logic.oracle.Oracle;
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
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.leialearns.common.Static.getLoggingClass;
import static org.leialearns.logic.oracle.TestUtilities.assertAtEnd;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ApplicationContext.xml","/org/leialearns/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class, ExecutionListener.class})
public class MinimizerTest {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Setting<String> interactionContextUri = new Setting<>("Interaction context URI");
    private final Setting<Session> session = new Setting<>("Session", this::createSession);

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private Root root;

    @Autowired
    private Oracle oracle;

    @Autowired
    private Minimizer minimizer;

    @Autowired
    private GraphDumper graphDumper;

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
    public void testMinimizer() {
        transactionHelper.runInTransaction(() -> {
            Session session = getSession();
            Version observedVersion = session.findLastVersion(ModelType.OBSERVED, AccessMode.READABLE);
            if (observedVersion != null) {
                Observed observed = observedVersion.createObservedVersion();
                logger.info("Observed: " + observed);
                Structure structure = session.getInteractionContext().getStructure();
                Node rootNode = structure.findRootNodes().first();
                assertNotNull("Root node", rootNode);
                Histogram histogram = observed.createHistogram(rootNode);
                logger.info("Minimizer: histogram: " + rootNode + ": {");
                Collection<Symbol> symbols = new TreeSet<>();
                for (Counter counter : histogram.getCounters()) {
                    symbols.add(counter.getSymbol());
                    logger.info("  " + counter);
                }
                logger.info("}");
                if (symbols.isEmpty()) {
                    logger.warn("Empty histogram");
                } else {
                    Expectation expectation = oracle.minimize(histogram);
                    if (expectation == null) {
                        logger.warn("No expectation");
                    } else {
                        expectation.log("Test expectation");
                        String prefixEncoding = expectation.prefixEncode(symbols);
                        String[] parts = prefixEncoding.split("\\|");
                        logger.info("Prefix encoding: {");
                        for (String part : parts) {
                            logger.info("  " + part);
                        }
                        logger.info("}");
                        Reader encodedReader = new StringReader(prefixEncoding);
                        Expectation decoded = oracle.prefixDecode(encodedReader, symbols);
                        assertAtEnd(encodedReader);
                        Fraction sum = root.createTransientFraction(0, 0, 1);
                        for (Symbol symbol : symbols) {
                            Fraction decodedFraction = decoded.getFraction(symbol);
                            assertEquals(expectation.getFraction(symbol), decodedFraction);
                            sum = oracle.add(sum, decodedFraction);
                        }
                        assertEquals(root.createTransientFraction(1, 1, 1), sum);
                    }
                    minimizer.command();
                    graphDumper.dumpGraph();
                }
            } else {
                logger.warn("No last observed version");
            }
        });
    }

}
