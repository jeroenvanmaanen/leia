package org.leialearns.logic.model;

import org.leialearns.api.enumerations.AccessMode;
import org.leialearns.api.enumerations.HistogramOperator;
import org.leialearns.api.enumerations.ModelType;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.api.model.Expected;
import org.leialearns.api.model.ExpectedModel;
import org.leialearns.api.model.Observed;
import org.leialearns.api.model.Toggled;
import org.leialearns.api.model.Version;
import org.leialearns.api.model.histogram.DeltaDiff;
import org.leialearns.api.model.histogram.DeltaDiffMap;
import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.api.model.histogram.HistogramFactory;
import org.leialearns.api.session.Session;
import org.leialearns.api.structure.Node;
import org.leialearns.bridge.BridgeOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.leialearns.api.enumerations.HistogramOperator.ADD_TO;
import static org.leialearns.logic.common.Static.getVersionOrdinal;
import static org.leialearns.common.Display.display;

public class DeltaHelper {
    private static final Logger logger = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());

    @Autowired
    private CheckHelper checkHelper;

    @Autowired
    private HistogramFactory histogramFactory;

    @BridgeOverride
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

            InteractionContext context = newObservedVersion.getInteractionContext();
            long maxToggledVersionId = getVersionOrdinal(newToggled);
            logger.debug("Max toggled version ID: " + maxToggledVersionId);
            Version.Iterable toggledVersions = context.findVersionsInRange(
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
                for (Map.Entry<Node, Boolean> entry : newToggledNodes.entrySet()) {
                    Node node = entry.getKey();
                    boolean willBeIncluded = entry.getValue();
                    boolean wasIncluded = expectedModel.isIncluded(node);
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
        checkHelper.check(CheckMode.PARTIAL, newObserved);
    }

    protected void adjustPathToRoot(Observed observed, ExpectedModel expected, Node node, boolean wasIncluded, boolean willBeIncluded) {
        logger.debug("  Adjust: " + display(node) + ": " + wasIncluded + " -> " + willBeIncluded);
        Histogram adjustment = observed.createTransientHistogram("adjustment");
        adjustment.add(observed.createHistogram(node));
        adjustment.subtract(observed.createDeltaHistogram(node));
        for (Node ancestor = node.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
            boolean ancestorWillBeIncluded = expected.isIncluded(ancestor);
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

    public void getDeltaDiff(DeltaDiffMap deltaDiffMap, Observed observed, ExpectedModel expectedModel) {
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
                    DeltaDiff mutation = histogramFactory.createDeltaDiff(node, "mutation");
                    HistogramOperator operator;
                    mutation.add(observed.createHistogram(node));
                    mutation.subtract(observed.createDeltaHistogram(node));
                    operator = ADD_TO.derive(isIncluded);
                    add(deltaDiffMap, node.getParent(), expectedModel, mutation, operator);
                }
            }
            logger.debug("}");
        }
    }

    public void add(DeltaDiffMap deltaDiffMap, Node node) {
        if (node != null) {
            add(deltaDiffMap, node.getParent());
            createDeltaDiff(deltaDiffMap, node);
        }
    }

    protected void add(DeltaDiffMap deltaDiffMap, Node node, ExpectedModel expectedModel, DeltaDiff mutation, HistogramOperator operator) {
        if (node != null) {
            if (expectedModel.isIncluded(node)) {
                add(deltaDiffMap, node.getParent());
            } else {
                add(deltaDiffMap, node.getParent(), expectedModel, mutation, operator);
            }
            DeltaDiff deltaDiff = createDeltaDiff(deltaDiffMap, node);
            logger.debug("  Mutation: " + mutation);
            mutation.modify(operator, deltaDiff);
        }
    }

    protected DeltaDiff createDeltaDiff(DeltaDiffMap deltaDiffMap, Node node) {
        DeltaDiff result = null;
        if (deltaDiffMap.containsKey(node)) {
            result = deltaDiffMap.get(node);
        }
        if (result == null) {
            Node parent = (node == null ? null : node.getParent());
            if (parent != null && (!deltaDiffMap.containsKey(parent) || deltaDiffMap.get(parent) == null)) {
                throw new IllegalArgumentException("Parent of node not in deltaDiffMap: " + node);
            }
            result = histogramFactory.createDeltaDiff(node, "add");
            deltaDiffMap.put(node, result);
            logger.debug("  New delta diff: " + result);
        } else {
            logger.debug("  Data diff exists: " + result);
        }
        return result;
    }

    public HashDeltaDiffMap createHashDeltaDiffMap() {
        return new HashDeltaDiffMap();
    }

    public class HashDeltaDiffMap extends HashMap<Node,DeltaDiff> implements DeltaDiffMap {}

    protected Map<Node, Boolean> getToggledNodes(Iterable<Version> versions) {
        return getToggledNodes(null, versions);
    }

    protected Map<Node, Boolean> getToggledNodes(String label, Iterable<Version> versions) {
        Map<Node, Boolean> toggledNodes = new TreeMap<>((Node node, Node other) -> {
            int result = node.getDepth() - other.getDepth();
            return (result == 0 ? node.compareTo(other) : result);
        });
        logger.debug((label == null || label.isEmpty() ? "Toggled" : label) + " versions: {");
        for (Version version : versions) {
            Toggled toggled = version.findToggledVersion();
            toggledNodes.put(toggled.getNode(), toggled.getInclude());
            logger.debug("  " + toggled);
        }
        logger.debug("}");
        return toggledNodes;
    }
}
