package org.leialearns.logic.model;

import org.leialearns.api.model.ExpectedModel;
import org.leialearns.api.model.Observed;
import org.leialearns.api.model.histogram.DeltaDiff;
import org.leialearns.api.model.histogram.DeltaDiffMap;
import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.api.structure.Node;
import org.leialearns.api.structure.Structure;
import org.leialearns.bridge.BridgeOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.leialearns.utilities.Display.displayParts;

public class CheckHelper {
    private static final Logger logger = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());

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
    public void check(Observed observed, DeltaDiffMap deltaDiffMap, ExpectedModel expectedModel) {
        check(CheckMode.FULL, observed, deltaDiffMap, expectedModel);
    }

    public void check(CheckMode mode, Observed observed, DeltaDiffMap deltaDiffMap, ExpectedModel expectedModel) {
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

    protected void check(CheckMode mode, Node node, Observed observed, DeltaDiffMap deltaDiffMap, ExpectedModel expectedModel) {
        Histogram sumObserved = observed.createTransientHistogram("sum observed");
        Histogram sumDelta = observed.createTransientHistogram("sum delta");
        check(mode, node, observed, deltaDiffMap, expectedModel, sumObserved, sumDelta);
    }

    protected void check(CheckMode mode, Node node, Observed observed, DeltaDiffMap deltaDiffMap, ExpectedModel expectedModel, Histogram parentObserved, Histogram parentDelta) {
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
