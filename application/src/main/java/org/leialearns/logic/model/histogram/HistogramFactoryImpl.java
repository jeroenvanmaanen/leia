package org.leialearns.logic.model.histogram;

import org.leialearns.api.model.histogram.DeltaDiff;
import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.api.model.histogram.HistogramFactory;
import org.leialearns.logic.structure.Node;
import org.springframework.stereotype.Component;

@Component
public class HistogramFactoryImpl implements HistogramFactory {

    @Override
    public Histogram createHistogram() {
        return new HistogramObject();
    }

    @Override
    public DeltaDiff createDeltaDiff(Node node, String label) {
        return new DeltaDiffImpl(node, label, this);
    }
}
