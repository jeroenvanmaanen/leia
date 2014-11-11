package org.leialearns.logic.model;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.model.common.NodeDataProxyImpl;
import org.leialearns.logic.model.common.NodeDataProxy;
import org.leialearns.logic.model.histogram.Counter;
import org.leialearns.logic.model.histogram.CounterUpdate;
import org.leialearns.logic.model.histogram.DeltaDiff;
import org.leialearns.logic.model.histogram.Histogram;
import org.leialearns.logic.model.histogram.HistogramObject;
import org.leialearns.logic.model.histogram.TransientCounter;
import org.leialearns.logic.session.Session;
import org.leialearns.logic.structure.Node;
import org.leialearns.logic.structure.Structure;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.leialearns.utilities.Display.asDisplay;
import static org.leialearns.utilities.Display.displayParts;

public class ObservedHelper {
    private static final Logger logger = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());

    @BridgeOverride
    public Histogram createTransientHistogram(Observed observed, String label) {
        Histogram result = createTransientHistogram(observed);
        result.setLabel(label);
        return result;
    }

    public Histogram createTransientHistogram(@SuppressWarnings("unused") Observed observed) {
        return createHistogram();
    }

    @BridgeOverride
    public Histogram createHistogram(Observed observed, Node node) {
        return createHistogram(observed.getVersion(), node, "Observed").getData();
    }

    @BridgeOverride
    public NodeDataProxy<Histogram> createHistogramProxy(Observed observed, Node node) {
        return createHistogram(observed.getVersion(), node, "Observed");
    }

    @BridgeOverride
    public Histogram createDeltaHistogram(Observed observed, Node node) {
        return createHistogram(observed.getDeltaVersion(), node, "Delta").getData();
    }

    public NodeDataProxy<Histogram> createHistogram(Version version, Node node, String label) {
        Session owner = version.getOwner();
        NodeDataProxyImpl<Histogram,Counter> proxy = new NodeDataProxyImpl<>();
        Histogram histogram;
        if (owner == null) {
            histogram = null;
        } else {
            histogram = owner.getHistogram(version, node);
            if (histogram != null) {
                histogram.retrieve(() -> version.findCounters(node));
            }
        }
        if (histogram == null) {
            histogram = createHistogram();
            if (label != null && label.length() > 0) {
                histogram.setLabel(label);
            }
            histogram.setLocation(() -> String.valueOf(node));
            histogram.setCounterCreator(
                    (Symbol symbol) ->
                            proxy.getPersistent()
                                    ? version.findOrCreateCounter(node, symbol)
                                    : new TransientCounter(symbol)
            );
            proxy.set(version, node);
            proxy.setItemsGetter(Version::findCounters);
            histogram.markPersistent();
        }
        if (owner != null) {
            owner.putHistogram(proxy);
        }
        return proxy;
    }

    @Bean
    @Scope(value = "prototype")
    public Histogram createHistogram() {
        return new HistogramObject();
    }

    @BridgeOverride
    public ExpectedModel getExpectedModel(Observed observed) {
        ExpectedModel result;
        Expected expected = observed.getExpected();
        Toggled toggled = observed.getToggled();
        if (expected != null && (toggled == null || toggled.getVersion().getOrdinal() < expected.getVersion().getOrdinal())) {
            result = expected;
        } else if (toggled != null) {
            result = toggled;
        } else {
            result = ExpectedModel.EMPTY;
        }
        return result;
    }

    @BridgeOverride
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void updateCounters(Observed newObserved, Observed oldObserved) {
        logger.debug("Update counters: [" + oldObserved + "] -> [" + newObserved + "]");
        // logger.trace("Stack trace", new Throwable());
        TypedIterable<CounterUpdate> counterUpdates = newObserved.findCounterUpdates(oldObserved);
        Version observedVersion = newObserved.getVersion();
        Version deltaVersion = newObserved.getDeltaVersion();
        ExpectedModel expectedModel = newObserved.getExpectedModel();
        logger.debug("Updating counters: {");
        for (CounterUpdate update : counterUpdates) {
            Counter counterDTO = update.getCounter();
            Node node = counterDTO.getNode();
            Symbol symbolDTO = counterDTO.getSymbol();
            long amount = update.getAmount();
            logger.debug("  Update: {} += {}", asDisplay(node), amount);
            boolean isCovered = false;
            do {
                if (counterDTO == null) {
                    logger.warn("  Counter not found: " + displayParts(node, symbolDTO));
                } else {
                    logger.debug("  (" + isCovered + ") " + counterDTO + " += " + amount);
                    counterDTO.increment(amount);
                    if (isCovered) {
                        Counter deltaCounter = deltaVersion.findOrCreateCounter(node, symbolDTO);
                        deltaCounter.increment(amount);
                    } else if (expectedModel.isIncluded(node)) {
                        isCovered = true;
                    }
                }
                node = node.getParent();
                counterDTO = observedVersion.findCounter(node, symbolDTO);
            } while (node != null);
        }
        logger.debug("}");
        Session owner = newObserved.getVersion().getOwner();
        if (owner != null) {
            owner.flush();
        }
        if (logger.isTraceEnabled()) {
            newObserved.logCounters();
            newObserved.check();
        }
    }

    @BridgeOverride
    protected ExpectedModel findLastExpectedModelBefore(Version limit) {
        Version expectedVersion = limit.findLastBefore(ModelType.EXPECTED, AccessMode.READABLE);
        Version toggledVersion = limit.findLastBefore(ModelType.TOGGLED, AccessMode.READABLE);
        ExpectedModel result;
        if (toggledVersion != null && (expectedVersion == null || toggledVersion.getOrdinal() > expectedVersion.getOrdinal())) {
            result = toggledVersion.findToggledVersion();
        } else if (expectedVersion != null) {
            result = expectedVersion.createExpectedVersion();
        } else {
            result = ExpectedModel.EMPTY;
        }
        return result;
    }

    @BridgeOverride
    public void check(Observed observed) {
        check(CheckMode.FULL, observed);
    }

    @BridgeOverride
    public void check(CheckMode mode, Observed observed) {
        ExpectedModel expectedModel = observed.getExpectedModel();
        check(mode, observed, null, expectedModel);
    }

    @BridgeOverride
    public void check(Observed observed, DeltaDiff.Map deltaDiffMap, ExpectedModel expectedModel) {
        check(CheckMode.FULL, observed, deltaDiffMap, expectedModel);
    }

    public void check(CheckMode mode, Observed observed, DeltaDiff.Map deltaDiffMap, ExpectedModel expectedModel) {
        Structure structure = observed.getVersion().getInteractionContext().getStructure();
        for (Node root : structure.findRootNodes()) {
            check(mode, root, observed, deltaDiffMap, expectedModel);
        }
        String suffix = "";
        if (deltaDiffMap != null && !deltaDiffMap.isEmpty()) {
            suffix = ": Delta diff size: " + deltaDiffMap.size();
        }
        if (expectedModel != observed.getExpectedModel()) {
            suffix = suffix + ": Expected model: " + expectedModel;
        }
        if (suffix.length() > 0) {
            suffix = ": (" + suffix.substring(2) + ")";
        }
        logger.debug("Observed model passed integrity check: " + observed + suffix);
    }

    @SuppressWarnings("unused")
    protected void check(CheckMode mode, Node node, Observed observed) {
        ExpectedModel expectedModel = observed.getExpectedModel();
        check(mode, node, observed, null, expectedModel);
    }

    protected void check(CheckMode mode, Node node, Observed observed, DeltaDiff.Map deltaDiffMap, ExpectedModel expectedModel) {
        Histogram sumObserved = observed.createTransientHistogram("sum observed");
        Histogram sumDelta = observed.createTransientHistogram("sum delta");
        check(mode, node, observed, deltaDiffMap, expectedModel, sumObserved, sumDelta);
    }

    protected void check(CheckMode mode, Node node, Observed observed, DeltaDiff.Map deltaDiffMap, ExpectedModel expectedModel, Histogram parentObserved, Histogram parentDelta) {
        boolean isIncluded = expectedModel.isIncluded(node);
        String context = displayParts(node,observed);

        // The sum of all observed histograms of all direct child nodes.
        Histogram sumObserved = observed.createTransientHistogram("sum observed: " + context);

        // The sum of all delta histograms of descendants for which there is no intermediate
        // node with a delta attached.
        Histogram sumDelta = observed.createTransientHistogram("sum delta: " + context);

        // 1) Check all descendants and gather information about them
        for (Node child : node.findChildren()) {
            check(mode, child, observed, deltaDiffMap, expectedModel, sumObserved, sumDelta);
            //parentObserved.add(observed.createHistogram(child));
        }

        // 2) Gather information about this node.
        logger.trace("Check: " + node + ": (" + isIncluded + ")");

        // 2.1) Get observed data and delta histograms.
        Histogram observedHistogram = observed.createHistogram(node);
        parentObserved.add(observedHistogram);
        Histogram deltaHistogram;
        DeltaDiff deltaDiff;
        if (deltaDiffMap != null && deltaDiffMap.containsKey(node) && (deltaDiff = deltaDiffMap.get(node)) != null) {
            deltaHistogram = observed.createTransientHistogram("Updated delta histogram");
            deltaHistogram.setLocation(() -> String.valueOf(node));
            deltaHistogram.add(observed.createDeltaHistogram(node));
            deltaHistogram.add(deltaDiff.getDeltaAdditions());
            deltaHistogram.subtract(deltaDiff.getDeltaSubtractions());
        } else {
            deltaHistogram = observed.createDeltaHistogram(node);
        }

        // 2.2) Check delta is the delta histogram that this node presents to its parents.
        Histogram checkDelta = observed.createTransientHistogram("check delta: " + context);
        if (isIncluded) {
            checkDelta.add(observedHistogram);
        } else {
            checkDelta.add(sumDelta);
        }
        parentDelta.add(checkDelta);

        // 3) Check this node
        checkIncluded(observed, node, deltaHistogram, observedHistogram);
        checkIncluded(observed, node, sumDelta, sumObserved);
        checkIncluded(observed, node, sumObserved, observedHistogram);

        // 3.1) deltaHistogram must be equal to checkDelta
        if (mode == CheckMode.FULL) {
            checkIncluded(observed, node, sumDelta, deltaHistogram);
        }
        checkIncluded(observed, node, deltaHistogram, sumDelta);
    }

    protected void checkIncluded(Observed observed, Node node, Histogram smallHistogram, Histogram bigHistogram) {
        Histogram difference = observed.createTransientHistogram("included difference");
        difference.setLocation(() -> String.valueOf(node));
        difference.add(bigHistogram);
        try {
            difference.subtract(smallHistogram); // Raises exception on underflow
        } catch (RuntimeException exception) {
            bigHistogram.log("big histogram");
            throw exception;
        }
    }

}
