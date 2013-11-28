package org.leialearns.bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.getLoggingClass;

/**
 * Registers types of objects that are used at both ends (bridge heads) of bridge objects as created by a
 * {@link org.leialearns.bridge.BridgeFactory BridgeFactory}.
 */
public class BridgeHeadTypeRegistry {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Map<Class<?>,Class<?>> nearToFarType = new HashMap<>();
    private final Map<Class<?>,Class<?>> farToNearType = new HashMap<>();
    private final Map<Class<?>,BridgeFactory> nearTypeToFactory = new HashMap<>();
    private final Map<Class<?>,BridgeFactory> farTypeToFactory = new HashMap<>();
    private final Map<Class<?>,Collection<FactoryAccessor<?>>> accessors = new HashMap<>();

    /**
     * Creates a new <code>BridgeHeadTypeRegistry</code> instance. The new instance makes itself known to
     * the given injector. This enables the injector to lazily provide
     * {@link org.leialearns.bridge.BridgeFactory BridgeFactory}s.
     * to newly created beans.
     * @param injector The injector to notify
     */
    @Autowired
    public BridgeHeadTypeRegistry(FactoryInjector injector) {
        injector.setRegistry(this);
    }

    /**
     * Registers the given bridge factory and its bridge head types.
     * @param bridgeFactory The bridge factory to register
     */
    public void register(BridgeFactory bridgeFactory) {
        Class<?> nearType = bridgeFactory.getNearType();
        Class<?> farType = bridgeFactory.getFarType();
        logger.trace(display(nearType) + ": " + display(farType));
        nearToFarType.put(nearType, farType);
        farToNearType.put(farType, nearType);
        nearTypeToFactory.put(nearType, bridgeFactory);
        farTypeToFactory.put(farType, bridgeFactory);
        if (accessors.containsKey(nearType)) {
            for (FactoryAccessor<?> accessor : accessors.get(nearType)) {
                injectInto(accessor);
            }
            accessors.remove(nearType);
        }
    }

    /**
     * Returns a flag that indicates whether the given class is registered as the near type of a
     * {@link org.leialearns.bridge.BridgeFactory BridgeFactory}.
     * @param nearType The class to check
     * @return <code>true</code> if the given class is registered as the near type of a bridge factory;
     *      <code>false</code> otherwise
     */
    public boolean hasNearType(Class<?> nearType) {
        return nearToFarType.containsKey(nearType);
    }

    /**
     * Returns a flag that indicates whether the given class is registered as the far type of a
     * {@link org.leialearns.bridge.BridgeFactory BridgeFactory}.
     * @param farType The class to check
     * @return <code>true</code> if the given class is registered as the far type of a bridge factory;
     *      <code>false</code> otherwise
     */
    public boolean hasFarType(Class<?> farType) {
        return farToNearType.containsKey(farType);
    }

    /**
     * Returns the far type that corresponds to the given near type.
     * @param nearType The near type to look up
     * @return The corresponding far type
     * @throws java.lang.IllegalStateException If the given near type is not registered
     */
    public Class<?> getFarType(Class<?> nearType) {
        if (!nearToFarType.containsKey(nearType)) {
            throw new IllegalStateException("Near type not registered: " + (nearType == null ? "null" : nearType.getSimpleName()));
        }
        return nearToFarType.get(nearType);
    }

    /**
     * Returns the bridge factory that corresponds to the given far type
     * @param farType The far type to look up
     * @return The corresponding bridge factory
     * @throws java.lang.IllegalStateException If the given far type is not registered
     */
    @SuppressWarnings("unchecked")
    public BridgeFactory getBridgeFactory(Class<?> farType) {
        if (!farToNearType.containsKey(farType)) {
            throw new IllegalStateException("Far type not registered: " + (farType == null ? "null" : farType.getSimpleName()));
        }
        return farTypeToFactory.get(farType);
    }

    /**
     * Inject the correct {@link org.leialearns.bridge.BridgeFactory BridgeFactory} into the given
     * accessor.
     * @param accessor The accessor to inject into
     */
    public void injectInto(FactoryAccessor<?> accessor) {
        logger.trace(display(accessor.getNearType()));
        Class<?> nearType = accessor.getNearType();
        if (nearTypeToFactory.containsKey(nearType)) {
            BridgeFactory factory = nearTypeToFactory.get(nearType);
            accessor.set(factory);
        } else {
            Collection<FactoryAccessor<?>> typedAccessors;
            if (accessors.containsKey(nearType)) {
                typedAccessors = accessors.get(nearType);
            } else {
                typedAccessors = new ArrayList<>();
                accessors.put(nearType, typedAccessors);
            }
            typedAccessors.add(accessor);
        }
    }

}
