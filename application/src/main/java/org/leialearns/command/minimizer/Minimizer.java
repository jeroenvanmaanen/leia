package org.leialearns.command.minimizer;

import org.leialearns.api.enumerations.AccessMode;
import org.leialearns.api.enumerations.HistogramOperator;
import org.leialearns.api.enumerations.ModelType;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.ExpectedModel;
import org.leialearns.api.model.Observed;
import org.leialearns.api.model.Toggled;
import org.leialearns.api.model.Version;
import org.leialearns.api.model.common.NodeDataProxy;
import org.leialearns.api.model.expectation.Expectation;
import org.leialearns.api.model.expectation.Fraction;
import org.leialearns.api.model.histogram.Counter;
import org.leialearns.api.model.histogram.DeltaDiff;
import org.leialearns.api.model.histogram.DeltaDiffMap;
import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.api.model.histogram.HistogramFactory;
import org.leialearns.api.structure.Node;
import org.leialearns.api.structure.Structure;
import org.leialearns.logic.model.CounterLogger;
import org.leialearns.logic.model.DeltaHelper;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.session.Session;
import org.leialearns.logic.utilities.Oracle;
import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.leialearns.api.enumerations.HistogramOperator.ADD_TO;
import static org.leialearns.api.enumerations.HistogramOperator.SUBTRACT_FROM;
import static org.leialearns.utilities.Static.getLoggingClass;

/**
 * Minimizes that total description length by optimizing the model of expected behavior.
 */
public class Minimizer implements org.leialearns.api.command.Minimizer {
    public static final double LOG_2 = Math.log(2.0);
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Setting<String> interactionContextUri = new Setting<>("Interaction context URI");

    @Autowired
    private Root root;

    @Autowired
    private Oracle oracle;

    @Autowired
    private DeltaHelper deltaHelper;

    @Autowired
    private HistogramFactory histogramFactory;

    @Autowired
    private CounterLogger counterLogger;

    /**
     * Sets the URL of the interaction context to use.
     * @param interactionContextUri The URL of the interaction context to use
     */
    public void setInteractionContextUri(String interactionContextUri) {
        this.interactionContextUri.set(interactionContextUri);
    }

    /**
     * Returns a new session object for this minimizer.
     * @return A new session object for this minimizer
     */
    protected Session createSession() {
        return root.createSession(interactionContextUri.get());
    }

    /**
     * Minimizes the model of expected behavior.
     * @param args The arguments to use
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void command(String... args) {
        logger.info(getLoggingClass(this).getSimpleName() + ".command()");
        Session session = createSession();
        Version lastToggled = session.findLastVersion(ModelType.TOGGLED, AccessMode.READABLE);
        Version lastExpected = session.findLastVersion(ModelType.EXPECTED, AccessMode.READABLE);
        ExpectedModel expectedModel;
        if (getVersionID(lastToggled) > getVersionID(lastExpected)) {
            expectedModel = lastToggled.findToggledVersion();
        } else if (lastExpected == null) {
            expectedModel = ExpectedModel.EMPTY;
        } else {
            expectedModel = lastExpected.createExpectedVersion();
        }
        Version lastObserved = session.findLastVersion(ModelType.OBSERVED, AccessMode.READABLE);
        Observed observed = lastObserved.createObservedVersion();
        Structure structure = session.getInteractionContext().getStructure();
        Node.Iterable rootNodes = structure.findRootNodes();
        DeltaDiffMap deltaDiffMap = deltaHelper.createHashDeltaDiffMap();
        deltaHelper.getDeltaDiff(deltaDiffMap, observed, expectedModel);
        if (logger.isDebugEnabled()) {
            observed.check(deltaDiffMap, expectedModel);
        }

        MinimizationContext context = new MinimizationContext(deltaDiffMap, observed, expectedModel, session);
        for (Node rootNode : rootNodes) {
            minimize(rootNode, context, null, null, null, null);
        }

        if (logger.isDebugEnabled()) {
            observed.check(deltaDiffMap, context.expectedModel);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    protected void minimize(Node node, MinimizationContext context, Node includedAncestor, NodeDataProxy<Histogram> ancestorObserved, Histogram ancestorDeltaBase, DeltaDiff ancestorDeltaDiff) {
        DeltaDiffMap deltaDiffMap = context.deltaDiffMap;
        Observed observed = context.observed;
        ExpectedModel expectedModel = context.expectedModel;
        DeltaDiff deltaDiff = deltaDiffMap.get(node);
        int depth = node.getDepth();

        if (depth == 1 && !expectedModel.isIncluded(node)) {
            Expectation expectation = oracle.minimize(observed.createHistogram(node));
            Toggled toggled = createToggled(node, expectation, null, null, true, observed, context.session);
            logger.debug("Created toggled for root node: {}: {}", node, toggled);
        }
        boolean nowIncluded = depth == 1 || expectedModel.isIncluded(node);
        Node subIncludedAncestor;
        NodeDataProxy<Histogram> observedHistogram = null;
        Histogram deltaBase = null;
        NodeDataProxy<Histogram> subAncestorObservedHistogram;
        Histogram subAncestorDeltaBase;
        DeltaDiff subAncestorDeltaDiff;
        if (includedAncestor == null || nowIncluded) {
            subIncludedAncestor = node;
            observedHistogram = observed.createHistogramProxy(node);
            subAncestorObservedHistogram = observedHistogram;
            deltaBase = observed.createDeltaHistogram(node);
            subAncestorDeltaBase = deltaBase;
            subAncestorDeltaDiff = deltaDiff;
        } else {
            subIncludedAncestor = includedAncestor;
            subAncestorObservedHistogram = ancestorObserved;
            subAncestorDeltaBase = ancestorDeltaBase;
            subAncestorDeltaDiff = ancestorDeltaDiff;
        }
        Node.Iterable children = node.findChildren();
        for (Node child : children) {
            minimize(child, context, subIncludedAncestor, subAncestorObservedHistogram, subAncestorDeltaBase, subAncestorDeltaDiff);
        }
        logger.debug("Minimize node: true:" + includedAncestor + " <- (" + depth + ")" + nowIncluded + ":" + node);

        if (depth > 1) {
            observedHistogram = (observedHistogram == null ? observed.createHistogramProxy(node) : observedHistogram);
            deltaBase = (deltaBase == null ? observed.createDeltaHistogram(node) : deltaBase);
            logger.debug("Evaluate node: " + node + " (" + nowIncluded + "/" + observedHistogram.getData().getWeight() + "/" + deltaBase.getWeight() + ")");
            if (deltaDiff == null) {
                // Could be added while minimizing children
                deltaDiff = deltaDiffMap.get(node);
            }
            if (ancestorDeltaDiff == null) {
                // Could be added while minimizing children
                ancestorDeltaDiff = deltaDiffMap.get(includedAncestor);
            }
            Toggled toggled = evaluate(nowIncluded, observed, observedHistogram, deltaBase, ancestorObserved, ancestorDeltaBase, deltaDiff, ancestorDeltaDiff, context.session);
            if (toggled != null) {
                logger.info("Toggle: " + node);
                expectedModel = context.expectedModel;
                context.expectedModel = toggled;
                DeltaDiff deltaChange = histogramFactory.createDeltaDiff(node, "toggle");
                deltaChange.add(observedHistogram.getData());
                deltaChange.subtract(deltaBase);
                if (deltaDiff != null) {
                    deltaDiff.subtractFrom(deltaChange);
                }

                HistogramOperator operator = SUBTRACT_FROM.derive(nowIncluded);

                deltaHelper.add(deltaDiffMap, node.getParent());
                Node a = node;
                do {
                    a = a.getParent();
                    if (a == null) {
                        break;
                    }
                    DeltaDiff aDeltaDiff = deltaDiffMap.get(a);
                    logger.debug("Apply delta change: " + a + ": " + aDeltaDiff);
                    deltaChange.modify(operator, aDeltaDiff);
                    logger.debug("Applied delta change: " + a + ": " + aDeltaDiff);
                } while (!expectedModel.isIncluded(a));

                if (logger.isTraceEnabled()) {
                    observed.check(deltaDiffMap, toggled);
                }
                context.session.flush();
            }
            if (logger.isDebugEnabled()) {
                long totalMemory = context.runtime.totalMemory();
                logger.debug("Total memory: " + totalMemory);
            }
        }
    }

    protected Toggled evaluate(boolean nowIncluded, Observed observed, NodeDataProxy<Histogram> observedHistogramProxy, Histogram deltaBase, NodeDataProxy<Histogram> ancestorObservedProxy, Histogram ancestorDeltaBase, DeltaDiff deltaDiff, DeltaDiff ancestorDeltaDiff, Session session) {
        final Histogram ancestorObserved = ancestorObservedProxy.getData();
        Iterable<Symbol> symbols = ancestorObserved.getSymbols();

        final Histogram ancestorData = observed.createTransientHistogram("Ancestor data");
        ancestorData.setLocation(() -> String.valueOf(ancestorObservedProxy.getNode()));
        ancestorData.add(ancestorObserved);
        if (ancestorDeltaDiff != null) {
            try {
                ancestorDeltaDiff.subtractFrom(ancestorData);
            } catch (IllegalStateException exception) {
                ancestorObserved.log("Ancestor observed");
                ancestorDeltaDiff.getDeltaAdditions().log("Ancestor delta additions");
                throw exception;
            }
        }
        ancestorData.subtract(ancestorDeltaBase);

        Expectation ancestorExpectation;
        ancestorExpectation = oracle.minimize(ancestorData);
        long currentDescriptionLength = descriptionLength(ancestorData, ancestorExpectation, symbols);
        long newDescriptionLength = 0L;

        DeltaDiff modification = histogramFactory.createDeltaDiff(null, "evaluate");
        modification.add(observedHistogramProxy.getData());
        if (deltaDiff != null) {
            deltaDiff.subtractFrom(modification);
        }
        modification.subtract(deltaBase);

        // Determine the data for this node as if it were included in the model of expected behavior
        Histogram data;
        data = observed.createTransientHistogram("Data");
        modification.addTo(data);

        Toggled result;
        if (data.isEmpty()) {
            result = null;
        } else {
            Expectation expectation = oracle.minimize(data);
            long dataDescriptionLength = descriptionLength(data, expectation, symbols);

            if (nowIncluded) {
                currentDescriptionLength += dataDescriptionLength;
            } else {
                newDescriptionLength += dataDescriptionLength;
            }

            // Modify the ancestor data to reflect the toggled node
            HistogramOperator operator = ADD_TO.derive(nowIncluded);
            modification.modify(operator, ancestorData);

            if (ancestorData.isEmpty()) {
                ancestorExpectation = null;
            } else {
                ancestorExpectation = oracle.minimize(ancestorData);
                newDescriptionLength += descriptionLength(ancestorData, ancestorExpectation, symbols);
            }
            // todo: take path from ancestor to descendant into account
            logger.debug("Current description length: " + currentDescriptionLength);
            logger.debug("New description length: " + newDescriptionLength);

            if (newDescriptionLength < currentDescriptionLength) {
                Node node = observedHistogramProxy.getNode();
                result = createToggled(node, expectation, ancestorObservedProxy.getNode(), ancestorExpectation, !nowIncluded, observed, session);
            } else {
                result = null;
            }
        }
        return result;
    }

    protected Toggled createToggled(Node node, Expectation expectation, Node ancestorNode, Expectation ancestorExpectation, boolean newIncluded, Observed observed, Session session) {
        Toggled result;
        logger.info("Toggle: " + node);
        result = session.createToggledVersion(node, newIncluded);
        result.setObserved(observed);
        if (ancestorNode != null) {
            result.attach(root, ancestorNode, ancestorExpectation);
        }
        if (newIncluded) {
            result.attach(root, node, expectation);
        }
        Version version = result.getVersion();
        version.setAccessMode(AccessMode.READABLE, session);
        counterLogger.logCounters(node, version);
        return result;
    }

    protected long getVersionID(Version version) {
        return version == null ? 0 : version.getOrdinal();
    }

    protected long descriptionLength(Histogram data, Expectation expectation, Iterable<Symbol> symbols) {
        long result = expectation.descriptionLength(symbols);
        double dataLength = 0.0;
        for (Counter counter : data.getCounters()) {
            long value = counter.getValue();
            if (value < 1) {
                continue;
            }
            Fraction probability = expectation.getFraction(counter.getSymbol());
            if (probability.getNumerator() < 1) {
                throw new IllegalArgumentException("The expectation does not cover the data: " + symbols + ": " + value + ": " + probability + ": ");
            }
            double approximation = ((double) probability.getNumerator()) / ((double) probability.getDenominator());
            double codeLength = -log2(approximation);
            dataLength += codeLength * ((double) value);
        }
        return result + ((long) Math.ceil(dataLength));
    }

    protected static double log2(double x) {
        return Math.log(x)/LOG_2;
    }

    protected class MinimizationContext {
        private final Runtime runtime = Runtime.getRuntime();
        private final DeltaDiffMap deltaDiffMap;
        private final Observed observed;
        private ExpectedModel expectedModel;
        private final Session session;
        protected MinimizationContext(DeltaDiffMap deltaDiffMap, Observed observed, ExpectedModel expectedModel, Session session) {
            this.deltaDiffMap = deltaDiffMap;
            this.observed = observed;
            this.expectedModel = expectedModel;
            this.session = session;
        }
    }

}
