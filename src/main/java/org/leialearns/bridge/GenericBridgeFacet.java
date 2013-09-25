package org.leialearns.bridge;

/**
 * Extends interface {@link org.leialearns.bridge.BridgeFacet BridgeFacet} with a method to set the encompassing
 * {@link org.leialearns.bridge.BridgeFacets} instance.
 */
public interface GenericBridgeFacet extends BridgeFacet {
    /**
     * Sets the <code>BridgeFacets</code> instance for this facet.
     * @param facets The <code>BridgeFacets</code> instance for this facet
     */
    void setBridgeFacets(BridgeFacets facets);
}
