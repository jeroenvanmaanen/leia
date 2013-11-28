package org.leialearns.bridge;

import org.leialearns.utilities.Expression;
import org.leialearns.utilities.Setting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Composes the near object, the far object, and intermediate stateful helpers that together comprise a bridge instance.
 */
public class BridgeFacets {
    private Setting<Object> nearObject = new Setting<>("Near Object", new Expression<Object>() {
        @Override
        public Object get() {
            return bridgeFactory.getNearObject(farObject);
        }
    });
    private final FarObject<?> farObject;
    private final BridgeFactory bridgeFactory;
    private final Map<Object, Object> helpers;

    /**
     * Creates a new <code>BridgeFacets</code> object. The near object is lazily created when necessary.
     *
     * @param farObject The far object for this bridge instance
     * @param bridgeFactory The bridge factory that can create the near object
     * @param helpers The helpers to include
     */
    public BridgeFacets(FarObject<?> farObject, BridgeFactory bridgeFactory, Map<Integer, Object> helpers) {
        this.farObject = farObject;
        this.bridgeFactory = bridgeFactory;
        this.helpers = (helpers == null ? Collections.emptyMap() : Collections.unmodifiableMap(new HashMap<Object, Object>(helpers)));
    }

    /**
     * Sets the near object to the given instance.
     * @param nearObject The given near object
     */
    public void setNearObject(Object nearObject) {
        this.nearObject.set(bridgeFactory.getNearType().cast(nearObject));
    }

    /**
     * Returns the near object of this bridge instance. The near object is created by the factory if needed.
     * @return The near object of this bridge instance
     */
    public Object getNearObject() {
        return nearObject.get();
    }

    /**
     * Returns an indication that tells whether the near object of this bridge instance is already fixated.
     * @return <code>true</code> if the near object of this bridge instance is already fixated; <code>false</code> otherwise
     */
    public boolean hasNearObject() {
        return nearObject.isFixated();
    }

    /**
     * Returns the far object of this bridge instance
     * @return The far object of this bridge instance
     */
    public FarObject<?> getFarObject() {
        return farObject;
    }

    /**
     * Returns the specified helper instance.
     * @param i The index of the helper instance
     * @return The specified helper instance
     */
    public Object getHelper(int i) {
        return helpers.get(i);
    }

    /*
    public <T> T getHelper(Class<T> type) {
        T result = null;
        for (Object helper : helpers.values()) {
            if (type.isInstance(helper)) {
                result = type.cast(helper);
                break;
            }
        }
        return result;
    }

    /**
     * Returns the <code>BridgeFactory</code> associated with this bridge instance.
     * @return The <code>BridgeFactory</code> associated with this bridge instance
     *!/
    public BridgeFactory getBridgeFactory() {
        return bridgeFactory;
    }
    */

}
