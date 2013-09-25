package org.leialearns.bridge;

/**
 * <p>Declares the methods that are needed to be part of a bridge instance. This interface is implemented by:</p>
 * <ol>
 *     <li>Generated near objects</li>
 *     <li>Far objects</li>
 *     <li>Instances of helpers of type Class.</li>
 * </ol>
 */
public interface BridgeFacet {
    /**
     * Returns the {@link org.leialearns.bridge.BridgeFacets BridgeFacets} object that provides access to all facets of a bridge instance.
     * @return The <code>BridgeFacets</code> instance for this facet
     */
    BridgeFacets getBridgeFacets();

    /**
     * Returns an indication whether this facet is part of a bridge instance. Especially far objects can
     * (even <em>must</em>) be created before they are part of a bridge instance. As long as no corresponding near
     * instance is created, this method returns <code>false</code>.
     * @return An indication whether this facet is part of a bridge instance
     */
    boolean hasBridgeFacets();
}
