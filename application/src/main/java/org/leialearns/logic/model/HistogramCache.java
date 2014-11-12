package org.leialearns.logic.model;

import org.leialearns.api.model.Version;
import org.leialearns.api.model.common.NodeDataProxy;
import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.api.structure.Node;
import org.leialearns.bridge.BaseBridgeFacet;

import java.util.HashMap;
import java.util.Map;

import static org.leialearns.utilities.Static.equal;

public class HistogramCache extends BaseBridgeFacet {
    private final Map<HistogramKey,Histogram> cache = new HashMap<>();

    public void putHistogram(NodeDataProxy<Histogram> histogramProxy) {
        Version version = histogramProxy.getVersion();
        Node node = histogramProxy.getNode();
        if (version != null && node != null) {
            HistogramKey key = createHistogramKey(version, node);
            cache.put(key, histogramProxy.getData());
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
            if (version == null) {
                throw new IllegalArgumentException("The version should not be null");
            }
            if (node == null) {
                throw new IllegalArgumentException("The node should not be null");
            }
            this.version = version;
            this.node = node;
        }

        @Override
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

        @Override
        public int hashCode() {
            return version.hashCode() + node.hashCode();
        }
    }
}
