package org.leialearns.command.minimizer;

import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.interaction.InteractionContext;
import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.model.histogram.Counter;
import org.leialearns.logic.model.histogram.CounterLogger;
import org.leialearns.logic.model.histogram.DeltaDiff;
import org.leialearns.logic.model.Expectation;
import org.leialearns.logic.model.Expected;
import org.leialearns.logic.model.ExpectedModel;
import org.leialearns.logic.model.Fraction;
import org.leialearns.logic.model.histogram.Histogram;
import org.leialearns.logic.model.Observed;
import org.leialearns.logic.model.Toggled;
import org.leialearns.logic.model.Version;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.session.Session;
import org.leialearns.logic.structure.Node;
import org.leialearns.logic.structure.Structure;
import org.leialearns.logic.utilities.Oracle;
import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.leialearns.logic.utilities.Static.getVersionOrdinal;
import static org.leialearns.utilities.Static.getLoggingClass;

/**
 * Minimizes that total description length by optimizing the model of expected behavior.
 */
public class Minimizer implements org.leialearns.command.api.Minimizer {
    public static final double LOG_2 = Math.log(2.0);
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Setting<String> interactionContextUri = new Setting<>("Interaction context URI");

    @Autowired
    private Root root;

    @Autowired
    private Oracle oracle;

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
        DeltaDiff.Map deltaDiffMap = createHashDeltaDiffMap();
        getDeltaDiff(deltaDiffMap, observed, expectedModel);
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
    protected void minimize(Node node, MinimizationContext context, Node includedAncestor, Histogram ancestorObserved, Histogram ancestorDeltaBase, DeltaDiff ancestorDeltaDiff) {
        DeltaDiff.Map deltaDiffMap = context.deltaDiffMap;
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
        Histogram observedHistogram = null;
        Histogram deltaBase = null;
        Histogram subAncestorObservedHistogram;
        Histogram subAncestorDeltaBase;
        DeltaDiff subAncestorDeltaDiff;
        if (includedAncestor == null || nowIncluded) {
            subIncludedAncestor = node;
            observedHistogram = observed.createHistogram(node);
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
            observedHistogram = (observedHistogram == null ? observed.createHistogram(node) : observedHistogram);
            deltaBase = (deltaBase == null ? observed.createDeltaHistogram(node) : deltaBase);
            logger.debug("Evaluate node: " + node + " (" + nowIncluded + "/" + observedHistogram.getWeight() + "/" + deltaBase.getWeight() + ")");
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
                DeltaDiff deltaChange = createDeltaDiff(node, observed, "toggle");
                deltaChange.add(observedHistogram);
                deltaChange.subtract(deltaBase);
                if (deltaDiff != null) {
                    deltaDiff.subtractFrom(deltaChange);
                }

                DeltaDiff.Operator operator = DeltaDiff.getOperatorSubtract(nowIncluded);

                add(deltaDiffMap, observed, node.getParent());
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

    protected Toggled evaluate(boolean nowIncluded, Observed observed, Histogram observedHistogram, Histogram deltaBase, Histogram ancestorObserved, Histogram ancestorDeltaBase, DeltaDiff deltaDiff, DeltaDiff ancestorDeltaDiff, Session session) {
        Collection<Symbol> symbols = getSymbols(ancestorObserved);

        Histogram ancestorData;
        Expectation ancestorExpectation;
        ancestorData = observed.createTransientHistogram("Ancestor data");
        ancestorData.setNode(ancestorObserved.getNode());
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
        ancestorExpectation = oracle.minimize(ancestorData);
        long currentDescriptionLength = descriptionLength(ancestorData, ancestorExpectation, symbols);
        long newDescriptionLength = 0L;

        DeltaDiff modification = createDeltaDiff(null, observed, "evaluate");
        modification.add(observedHistogram);
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
            DeltaDiff.Operator operator = DeltaDiff.getOperatorAdd(nowIncluded);
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
                Node node = observedHistogram.getNode();
                result = createToggled(node, expectation, ancestorData.getNode(), ancestorExpectation, !nowIncluded, observed, session);
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

    protected long descriptionLength(Histogram data, Expectation expectation, Collection<Symbol> symbols) {
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

    protected Collection<Symbol> getSymbols(Histogram histogram) {
        Collection<Symbol> result = new HashSet<>();
        for (Counter counter : histogram.getCounters()) {
            result.add(counter.getSymbol());
        }
        return result;
    }

    protected void getDeltaDiff(DeltaDiff.Map deltaDiffMap, Observed observed, ExpectedModel expectedModel) {
        Toggled oldToggled = observed.getToggled();
        Toggled newToggled;
        if (expectedModel instanceof Expected) {
            newToggled = ((Expected) expectedModel).getToggled();
        } else if (expectedModel instanceof Toggled) {
            newToggled = (Toggled) expectedModel;
        } else {
            newToggled = null;
        }
        ExpectedModel oldExpectedModel = observed.getExpectedModel();
        logger.debug("New toggled: " + newToggled);
        if (newToggled != null) {
            long oldToggledOrdinal = getVersionOrdinal("Old toggled", oldToggled);
            long newToggledOrdinal = getVersionOrdinal("New toggled", newToggled);
            logger.debug("Range: " + oldToggledOrdinal + "..." + newToggledOrdinal + "{");
            InteractionContext context = observed.getVersion().getInteractionContext();
            Version.Iterable versions = context.findVersionsInRange(oldToggledOrdinal, newToggledOrdinal, ModelType.TOGGLED, AccessMode.READABLE);
            Map<Node,Toggled> toggledNodes = new LinkedHashMap<>(16, 0.75f, true);
            for (Version version : versions) {
                Toggled toggled = version.findToggledVersion();
                if (toggled == null) {
                    logger.warn("No toggled version extension found for version: " + version);
                } else {
                    toggledNodes.put(toggled.getNode(), toggled);
                    logger.debug("  Toggled: " + toggled);
                }
            }
            logger.debug("} now process {");
            for (Toggled toggled : toggledNodes.values()) {
                logger.debug("  Toggled: " + toggled);
                Node node = toggled.getNode();
                boolean wasIncluded = oldExpectedModel.isIncluded(node);
                boolean isIncluded = expectedModel.isIncluded(node);
                if (isIncluded != wasIncluded) {
                    logger.debug("  " + wasIncluded + " -> " + isIncluded);
                    DeltaDiff mutation = createDeltaDiff(node, observed, "mutation");
                    DeltaDiff.Operator operator;
                    mutation.add(observed.createHistogram(node));
                    mutation.subtract(observed.createDeltaHistogram(node));
                    operator = DeltaDiff.getOperatorAdd(isIncluded);
                    add(deltaDiffMap, observed, node.getParent(), expectedModel, mutation, operator);
                }
            }
            logger.debug("}");
        }
    }

    protected void add(DeltaDiff.Map deltaDiffMap, Observed observed, Node node) {
        if (node != null) {
            add(deltaDiffMap, observed, node.getParent());
            createDeltaDiff(deltaDiffMap, node, observed);
        }
    }

    protected void add(DeltaDiff.Map deltaDiffMap, Observed observed, Node node, ExpectedModel expectedModel, DeltaDiff mutation, DeltaDiff.Operator operator) {
        if (node != null) {
            if (expectedModel.isIncluded(node)) {
                add(deltaDiffMap, observed, node.getParent());
            } else {
                add(deltaDiffMap, observed, node.getParent(), expectedModel, mutation, operator);
            }
            DeltaDiff deltaDiff = createDeltaDiff(deltaDiffMap, node, observed);
            logger.debug("  Mutation: " + mutation);
            mutation.modify(operator, deltaDiff);
        }
    }

    protected DeltaDiff createDeltaDiff(DeltaDiff.Map deltaDiffMap, Node node, Observed observed) {
        DeltaDiff result = null;
        if (deltaDiffMap.containsKey(node)) {
            result = deltaDiffMap.get(node);
        }
        if (result == null) {
            Node parent = (node == null ? null : node.getParent());
            if (parent != null && (!deltaDiffMap.containsKey(parent) || deltaDiffMap.get(parent) == null)) {
                throw new IllegalArgumentException("Parent of node not in deltaDiffMap: " + node);
            }
            result = createDeltaDiff(node, observed, "add");
            deltaDiffMap.put(node, result);
            logger.debug("  New delta diff: " + result);
        } else {
            logger.debug("  Data diff exists: " + result);
        }
        return result;
    }

    protected DeltaDiff createDeltaDiff(Node node, Observed observed, String label) {
        return new DeltaDiff(node, observed, label);
    }

    protected class MinimizationContext {
        private final Runtime runtime = Runtime.getRuntime();
        private final DeltaDiff.Map deltaDiffMap;
        private final Observed observed;
        private ExpectedModel expectedModel;
        private final Session session;
        protected MinimizationContext(DeltaDiff.Map deltaDiffMap, Observed observed, ExpectedModel expectedModel, Session session) {
            this.deltaDiffMap = deltaDiffMap;
            this.observed = observed;
            this.expectedModel = expectedModel;
            this.session = session;
        }
    }

    protected HashDeltaDiffMap createHashDeltaDiffMap() {
        return new HashDeltaDiffMap();
    }

    protected class HashDeltaDiffMap extends HashMap<Node,DeltaDiff> implements DeltaDiff.Map {}

}
