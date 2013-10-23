package org.leialearns.bridge;

/**
 * Provides static convenience methods that pertain to the bridge factory package.
 */
public class Static {

    private Static() {
        throw new UnsupportedOperationException("This class must not be instantiated: " + getClass().getSimpleName());
    }

    /**
     * Returns the {@link BridgeFacets} container object for the given bridge facet.
     * @param bridgeFacet The facet of the bridge object
     * @return The bridge facets container
     */
    public static BridgeFacets getFacets(Object bridgeFacet) {
        return ((BridgeFacet) bridgeFacet).getBridgeFacets();
    }

    /**
     * Returns the far object that corresponds to the given near object in a type safe way.
     * @param nearObject The near object
     * @param farType The type of the far object
     * @return The far object that corresponds to the given near object
     * @see BridgeFacets#getFarObject()
     */
    public static <FT extends FarObject<NT>, NT> FT getFarObject(NT nearObject, Class<FT> farType) {
        return nearObject == null ? null : farType.cast(getFarObject(nearObject));
    }

    /**
     * Returns the far object that corresponds to the given near object.
     * @param nearObject The near object
     * @return The far object that corresponds to the given near object
     * @see BridgeFacets#getFarObject()
     */
    public static FarObject<?> getFarObject(Object nearObject) {
        return nearObject == null ? null : getFacets(nearObject).getFarObject();
    }

}
