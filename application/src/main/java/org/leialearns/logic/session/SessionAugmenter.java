package org.leialearns.logic.session;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.api.model.common.NodeDataProxy;
import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.logic.model.HistogramCache;
import org.leialearns.logic.model.Version;
import org.leialearns.logic.structure.Node;

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
     * <em>See {@link org.leialearns.logic.session.Session#getHistogram(org.leialearns.logic.model.Version, org.leialearns.logic.structure.Node)}.</em>
     */
    @BridgeOverride
    public Histogram getHistogram(Version version, Node node) {
        return histogramCache.getHistogram(version, node);
    }

}
