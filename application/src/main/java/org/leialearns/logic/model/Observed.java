package org.leialearns.logic.model;

import org.leialearns.api.model.common.NodeDataProxy;
import org.leialearns.api.model.histogram.CounterUpdate;
import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.api.model.histogram.DeltaDiffMap;
import org.leialearns.api.structure.Node;
import org.leialearns.utilities.TypedIterable;

public interface Observed extends TypedVersionExtension {
    Version getVersion();

    @SuppressWarnings("unused")
    Version getCountedVersion();

    Counted getCounted();
    Version getDeltaVersion();
    Toggled getToggled();
    void setExpected(Expected expected);
    Expected getExpected();
    ExpectedModel getExpectedModel();
    Version getOrCreateDelta();

    @SuppressWarnings("unused")
    Histogram createTransientHistogram();

    Histogram createTransientHistogram(String label);
    Histogram createHistogram(Node node);
    NodeDataProxy<Histogram> createHistogramProxy(Node node);
    Histogram createDeltaHistogram(Node node);
    void attachCounted(Observed oldObserved);
    void attachToggled();
    void copyCountersFromLastObserved(Version lastObserved);
    void createCountersFromRecentCounted(Observed oldObserved);
    TypedIterable<CounterUpdate> findCounterUpdates(Observed oldObserved);
    void updateCounters(Observed oldObserved);
    void copyCountersFromLastDelta(Observed oldObserved);
    void adjustDeltaForToggledNodes(Observed oldObserved);
    void logCounters();
    void logCounters(Node node);
    void check();
    void check(DeltaDiffMap deltaDiffMap, ExpectedModel expectedModel);
}
