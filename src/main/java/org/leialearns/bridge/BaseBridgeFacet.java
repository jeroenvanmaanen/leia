package org.leialearns.bridge;

import org.leialearns.utilities.Setting;

/**
 * Convenience class that can be used as a super class for classes that should implement
 * {@link org.leialearns.bridge.GenericBridgeFacet}.
 */
public class BaseBridgeFacet implements GenericBridgeFacet {

    private final Setting<BridgeFacets> facets = new Setting<>("Facets");

    @Override
    public void setBridgeFacets(BridgeFacets facets) {
        this.facets.set(facets);
    }

    @Override
    public BridgeFacets getBridgeFacets() {
        return facets.get();
    }

    @Override
    public boolean hasBridgeFacets() {
        return facets.isFixated();
    }
}
