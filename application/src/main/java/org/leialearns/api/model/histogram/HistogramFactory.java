package org.leialearns.api.model.histogram;

import org.leialearns.logic.structure.Node;

public interface HistogramFactory {
    Histogram createHistogram();
    DeltaDiff createDeltaDiff(Node node, String label);
}
