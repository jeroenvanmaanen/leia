package org.leialearns.logic.model;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.logic.structure.Node;

import java.util.HashMap;
import java.util.Map;

import static org.leialearns.utilities.Static.equal;

public class HistogramCache extends BaseBridgeFacet {
    private final Map<HistogramKey,Histogram> cache = new HashMap<HistogramKey, Histogram>();

    public void putHistogram(Histogram histogram) {
        Version version = histogram.getVersion();
        Node node = histogram.getNode();
        if (version != null && node != null) {
            HistogramKey key = createHistogramKey(version, node);
            cache.put(key, histogram);
        }
    }

    public Histogram getHistogram(Version version, Node node) {
        return cache.get(createHistogramKey(version, node));
    }

    protected HistogramKey createHistogramKey(Version version, Node node) {
        return new HistogramKey(version, node);
    }

    protected class HistogramKey {
        private final Version version;
        private final Node node;

        protected HistogramKey(Version version, Node node) {
            this.version = version;
            this.node = node;
        }

        public boolean equals(Object otherObject) {
            boolean result;
            if (otherObject instanceof HistogramKey) {
                HistogramKey other = (HistogramKey) otherObject;
                result = equal(version, other.version) && equal(node, other.node);
            } else {
                result = false;
            }
            return result;
        }

        public int hashCode() {
            return version.hashCode() + node.hashCode();
        }
    }
}
