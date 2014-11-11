package org.leialearns.logic.model;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.model.common.NodeDataProxyImpl;
import org.leialearns.logic.model.common.NodeDataProxy;
import org.leialearns.logic.model.histogram.Counter;
import org.leialearns.logic.model.histogram.CounterUpdate;
import org.leialearns.logic.model.histogram.Histogram;
import org.leialearns.logic.model.histogram.HistogramObject;
import org.leialearns.logic.model.histogram.TransientCounter;
import org.leialearns.logic.session.Session;
import org.leialearns.logic.structure.Node;
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

}
