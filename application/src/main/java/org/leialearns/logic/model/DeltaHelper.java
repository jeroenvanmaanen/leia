package org.leialearns.logic.model;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.interaction.InteractionContext;
import org.leialearns.logic.model.histogram.Histogram;
import org.leialearns.logic.session.Session;
import org.leialearns.logic.structure.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import static org.leialearns.logic.utilities.Static.getVersionOrdinal;
import static org.leialearns.utilities.Display.display;

public class DeltaHelper {
    private static final Logger logger = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());

    @Autowired
    private CheckHelper checkHelper;

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

    protected Map<Node, Boolean> getToggledNodes(Iterable<Version> versions) {
        return getToggledNodes(null, versions);
    }

    protected Map<Node, Boolean> getToggledNodes(String label, Iterable<Version> versions) {
        Map<Node, Boolean> toggledNodes = new TreeMap<>(createShallowFirst());
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
}
