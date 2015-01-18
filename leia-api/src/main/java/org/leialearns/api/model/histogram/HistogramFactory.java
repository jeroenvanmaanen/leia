package org.leialearns.api.model.histogram;

import org.leialearns.api.structure.Node;

public interface HistogramFactory {
    Histogram createHistogram();
    DeltaDiff createDeltaDiff(Node node, String label);
}
