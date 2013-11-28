package org.leialearns.bridge;

import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.getLoggingClass;

/**
 * Provides type-safe access to the {@link BridgeFactory} of a given near type. The factory bean has to be injected,
 * <em>i.e.</em>, by using a {@link FactoryInjector} bean.
 * @param <NT> The near type of the bridge factory
 */
public class FactoryAccessor<NT> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Class<NT> nearType;
    private Setting<BridgeFactory> factorySetting = new Setting<>("Bridge Factory");

    /**
     * Creates a new <code>FactoryAccessor</code> instance for the given near type.
     * @param type The near type that has to match the bridge factory
     */
    public FactoryAccessor(Class<NT> type) {
        this.nearType = type;
    }

    /**
     * Sets the bridge factory to use with this accessor. The near type of the given bridge factory has to match
     * the near type of this bridge accessor.
     * @param factory The bridge factory to set
     */
    public void set(BridgeFactory factory) {
        if (factory.getNearType() != nearType) {
            throw new IllegalArgumentException("Type mismatch: " + nearType + ": " + factory.getNearType());
        }
        factorySetting.set(factory);
    }

    /**
     * Returns the bridge factory for this accessor.
     * @return The bridge factory for this accessor
     */
    public BridgeFactory get() {
        BridgeFactory result;
        try {
            result = factorySetting.get();
        } catch (RuntimeException exception) {
            RuntimeException wrapper = new IllegalStateException("Missing factory for: " + display(nearType), exception);
            logger.trace("Stack trace", wrapper);
            throw wrapper;
        }
        return result;
    }

    /**
     * Returns the near type that corresponds to this accessor.
     * @return The near type that corresponds to this accessor
     */
    public Class<?> getNearType() {
        return nearType;
    }

    /**
     * Returns the near object that corresponds to the given far object. The types must match the bridge factory
     * associated with this accessor and the bridge factory must be set.
     * @param farObject The far object to look up
     * @return The corresponding near object
     */
    public NT getNearObject(Object farObject) {
        return nearType.cast(factorySetting.get().getNearObject(farObject));
    }

}
