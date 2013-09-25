package org.leialearns.logic.model;

import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.interaction.Symbol;
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

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import static org.leialearns.logic.utilities.Static.getVersionOrdinal;
import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Display.displayParts;
import static org.leialearns.utilities.Static.getLoggingClass;

public class ObservedHelper {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    public Histogram createTransientHistogram(Observed observed, String label) {
        Histogram result = createTransientHistogram(observed);
        result.setLabel(label);
        return result;
    }

    public Histogram createTransientHistogram(Observed observed) {
        return createHistogram();
    }

    public Histogram createHistogram(Observed observed, Node node) {
        return createHistogram(observed.getVersion(), node, "Observed");
    }

    public Histogram createDeltaHistogram(Observed observed, Node node) {
        return createHistogram(observed.getDeltaVersion(), node, "Delta");
    }

    public Histogram createHistogram(Version version, Node node, String label) {
        Session owner = version.getOwner();
        Histogram result;
        if (owner == null) {
            result = null;
        } else {
            result = owner.getHistogram(version, node);
            if (result != null) {
                result.retrieve();
            }
        }
        if (result == null) {
            result = createHistogram();
            if (label != null && label.length() > 0) {
                result.setLabel(label);
            }
            result.set(version, node);
        }
        if (owner != null) {
            owner.putHistogram(result);
        }
        return result;
    }

    @Bean
    @Scope(value = "prototype")
    public Histogram createHistogram() {
        return new HistogramObject();
    }

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
                    } else if (expectedModel.isIncluded(node, newObserved.getVersion().getOwner())) {
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

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void adjustDeltaForToggledNodes(Observed newObserved, Observed oldObserved) {
        logger.debug("Adjust delta for toggled nodes: [" + oldObserved + "] -> [" + newObserved + "]");
        Version newObservedVersion = newObserved.getVersion();
        Version oldObservedVersion = oldObserved == null ? null : oldObserved.getVersion();
        logger.debug("Old observed version: [" + oldObservedVersion + "]");
        newObserved.attachToggled();
        Toggled newToggled = newObserved.getToggled();
        logger.debug("New toggled: [" + newToggled + "]");
        if (newToggled != null) {
            ExpectedModel expectedModel = (oldObserved == null ? null : oldObserved.getExpectedModel());
            if (expectedModel instanceof Expected) {
                expectedModel = ((Expected) expectedModel).getToggled();
            }
            Long oldToggledVersionId = getVersionOrdinal("Old toggled", expectedModel);
            long minToggledVersionId = (oldToggledVersionId == null ? 0 : oldToggledVersionId + 1);
            logger.debug("Min toggled version ID: " + minToggledVersionId);
            if (expectedModel == null) {
                expectedModel = ExpectedModel.EMPTY;
            }

            Session owner = newObservedVersion.getOwner();
            long maxToggledVersionId = getVersionOrdinal(newToggled);
            logger.debug("Max toggled version ID: " + maxToggledVersionId);
            Version.Iterable toggledVersions = owner.findVersionsInRange(
                    minToggledVersionId,
                    maxToggledVersionId,
                    ModelType.TOGGLED,
                    null
            );
            Version first = toggledVersions.first();

            if (first != null) {
                if (logger.isTraceEnabled() && oldObserved != null) {
                    oldObserved.check();
                }
                logger.debug("First newly toggled version: " + first);
                // ExpectedModel expectedModel = oldObserved.getExpectedModel(); // findLastExpectedModelBefore(first);
                logger.debug("Expected model: " + expectedModel);
                Map<Node, Boolean> newToggledNodes = getToggledNodes(toggledVersions);
                if (logger.isTraceEnabled()) {
                    logger.trace("New toggled nodes: {");
                    for (Node node : newToggledNodes.keySet()) {
                        logger.trace("  " + node);
                    }
                    logger.trace("}");
                }
                Session session = newObserved.getVersion().getOwner();
                for (Map.Entry<Node, Boolean> entry : newToggledNodes.entrySet()) {
                    Node node = entry.getKey();
                    boolean willBeIncluded = entry.getValue();
                    boolean wasIncluded = expectedModel.isIncluded(node, session);
                    logger.trace("New toggled node: " + node + ": " + wasIncluded + " -> " + willBeIncluded);
                    if (willBeIncluded != wasIncluded) {
                        adjustPathToRoot(newObserved, newToggled, node, wasIncluded, willBeIncluded);
                    }
                }
            }
        }
        Session owner = newObserved.getVersion().getOwner();
        if (owner != null) {
            owner.flush();
        }
        check(CheckMode.PARTIAL, newObserved);
    }

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

    protected void adjustPathToRoot(Observed observed, ExpectedModel expected, Node node, boolean wasIncluded, boolean willBeIncluded) {
        logger.debug("  Adjust: " + display(node) + ": " + wasIncluded + " -> " + willBeIncluded);
        Histogram adjustment = observed.createTransientHistogram("adjustment");
        adjustment.add(observed.createHistogram(node));
        adjustment.subtract(observed.createDeltaHistogram(node));
        Session session = observed.getVersion().getOwner();
        for (Node ancestor = node.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
            boolean ancestorWillBeIncluded = expected.isIncluded(ancestor, session);
            Histogram ancestorDelta = observed.createDeltaHistogram(ancestor);
            logger.debug("  Modify: " + ancestor + " (" + ancestorWillBeIncluded + ")");
            if (willBeIncluded) {
                ancestorDelta.add(adjustment);
            } else {
                ancestorDelta.subtract(adjustment);
            }
            if (ancestorWillBeIncluded) {
                break;
            }
        }
    }

    public void check(Observed observed) {
        check(CheckMode.FULL, observed);
    }

    public void check(CheckMode mode, Observed observed) {
        ExpectedModel expectedModel = observed.getExpectedModel();
        check(mode, observed, null, expectedModel);
    }

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
        boolean isIncluded = expectedModel.isIncluded(node, observed.getVersion().getOwner());
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
            deltaHistogram.setNode(node);
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
        difference.setNode(node);
        difference.add(bigHistogram);
        try {
            difference.subtract(smallHistogram); // Raises exception on underflow
        } catch (RuntimeException exception) {
            bigHistogram.log("big histogram");
            throw exception;
        }
    }

    protected Map<Node, Boolean> getToggledNodes(Iterable<Version> versions) {
        return getToggledNodes(null, versions);
    }

    protected Map<Node, Boolean> getToggledNodes(String label, Iterable<Version> versions) {
        Map<Node, Boolean> toggledNodes = new TreeMap<Node, Boolean>(createShallowFirst());
        logger.debug((label == null || label.isEmpty() ? "Toggled" : label) + " versions: {");
        for (Version version : versions) {
            Toggled toggled = version.findToggledVersion();
            toggledNodes.put(toggled.getNode(), toggled.getInclude());
            logger.debug("  " + toggled);
        }
        logger.debug("}");
        return toggledNodes;
    }

    protected ShallowFirst createShallowFirst() {
        return new ShallowFirst();
    }

    protected class ShallowFirst implements Comparator<Node> {

        @Override
        public int compare(Node node, Node other) {
            int result = node.getDepth() - other.getDepth();
            return (result == 0 ? node.compareTo(other) : result);
        }

    }

    protected enum CheckMode {
        FULL,
        PARTIAL,
    }

}
