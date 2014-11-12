package org.leialearns.logic.session;

import org.leialearns.api.model.Version;
import org.leialearns.api.model.common.NodeDataProxy;
import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.api.structure.Node;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.logic.model.HistogramCache;

/**
 * Augments {@link Session} objects with methods to cache histograms.
 */
public class SessionAugmenter {
    private final HistogramCache histogramCache = new HistogramCache();

    /*
     * <em>See {@link org.leialearns.logic.session.Session#putHistogram(Histogram)}.</em>
     */
    @BridgeOverride
    public void putHistogram(NodeDataProxy<Histogram> histogram) {
        histogramCache.putHistogram(histogram);
    }

    /*
     * <em>See {@link org.leialearns.logic.session.Session#getHistogram(org.leialearns.api.model.Version, org.leialearns.api.structure.Node)}.</em>
     */
    @BridgeOverride
    public Histogram getHistogram(Version version, Node node) {
        return histogramCache.getHistogram(version, node);
    }

}
