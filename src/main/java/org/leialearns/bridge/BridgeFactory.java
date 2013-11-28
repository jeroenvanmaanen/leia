package org.leialearns.bridge;

import org.leialearns.utilities.ExceptionWrapper;
import org.leialearns.utilities.Expression;
import org.leialearns.utilities.Function;
import org.leialearns.utilities.HasWrappedIterable;
import org.leialearns.utilities.Setting;
import org.leialearns.utilities.TransformingIterable;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.leialearns.bridge.Static.getFarObject;
import static org.leialearns.utilities.Display.asDisplayWithTypes;
import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Display.displayWithTypes;
import static org.leialearns.utilities.Display.displayParts;
import static org.leialearns.utilities.Static.getLoggingClass;
import static org.leialearns.utilities.Static.offer;

/**
 * <p>Provides a factory with supporting classes that generates implementations of a logical interface whose methods are
 * delegated to various helper objects on both sides of an abstraction separation. See {@link org.leialearns.bridge}.</p>
 *
 * <p>This class is designed for use in Spring to define several beans, one for each logical interface
 * (<em>near type</em>). A bridge factory bean is defined by its <em>far type</em>, <em>i.e.</em>, the type of
 * its concrete delegation index. The logical interface is determined by reflection (by looking up the return
 * type of the {@link DeclaresNearType#declareNearType() declareNearType} method.</p>
 *
 * <p>Additional helpers can be specified as desired. Bindings for the methods specified in the logical interface
 * are looked up when the factory is instantiated in the order in which the helpers are given. If a method is not found
 * in one of the helpers it is looked up in the far type. If it is desired to search the far type before some of the
 * helpers, include a null pointer in the array of helpers.</p>
 *
 * <p>There are five types of helpers:</p>
 * <ol>
 * <li>Helper object that uses near types exclusively</li>
 * <li>Class object</li>
 * <li>Helper object that uses far types exclusively</li>
 * <li>Helper object that mixes near and far types</li>
 * <li><code>null</code></li>
 * </ol>
 * <p>For helpers of type 2 a new instance is created using the default no-arg constructor for each bridge object
 * that is created by the factory. These instances are <em>not</em> configured by the spring bean factory. The helper
 * <code>null</code> represents the far object. For the other three types of helpers, either the near object or the
 * far object is prepended to the parameter list, as appropriate. The use of helpers of type 4 is strongly
 * discouraged.</p>
 *
 * <p>In the context of an ORM framework it is common to use not only the DAO of that corresponds the far type as a
 * helper, but also DAOs of other entities. For example, the SymbolDAO can provide a method that finds all
 * symbols that belong to a particular alphabet. This method can be exposed very naturally on the Alphabet bridged
 * object.</p>
 *
 * <h3>Spring configuration example</h3>
 * <pre>
 *    &lt;bean id="alphabetFactory" class="org.leialearns.bridge.BridgeFactory"&gt;
 *      &lt;constructor-arg value="org.leialearns.jpa.interaction.AlphabetDTO"/&gt;
 *      &lt;constructor-arg&gt;
 *        &lt;array&gt;
 *          &lt;ref bean="alphabetDAO"/&gt;
 *          &lt;ref bean="symbolDAO"/&gt;
 *          &lt;null/&gt; &lt;!-- AlphabetDTO --&gt;
 *          &lt;value type="java.lang.Class"&gt;org.leialearns.logic.interaction.AlphabetAugmenter&lt;/value&gt;
 *        &lt;/array&gt;
 *      &lt;/constructor-arg&gt;
 *    &lt;/bean&gt;
 * </pre>
 *
 * @see org.leialearns.bridge
 */
public class BridgeFactory {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Class<?> nearType;
    private final Class<? extends FarObject<?>> farType;
    private final Object[] helpers;
    private final Map<Method, Binding> methodMap = new HashMap<>();
    private final Method facetsGetter;
    private final Method facetsChecker;

    private BridgeHeadTypeRegistry registry;

    /**
     * Creates a new BridgeFactory instance.
     *
     * @param farTypeName The name of the far type class
     * @param helpers The helpers that implement additional methods on the near type
     */
    public BridgeFactory(String farTypeName, Object... helpers) {
        this(helpers, getFarType(farTypeName));
    }

    /**
     * Creates a new BridgeFactory instance.
     *
     * @param farType The far type class
     * @param helpers The helpers that implement additional methods on the near type
     */
    public BridgeFactory(Class<? extends FarObject<?>> farType, Object... helpers) {
        this(helpers, farType);
    }

    protected BridgeFactory(Object[] helpers, Class<? extends FarObject<?>> farType) {
        this.helpers = (helpers == null ? new Object[0] : helpers);
        this.farType = farType;
        nearType = getNearType(farType);
        if (logger.isTraceEnabled()) {
            logger.trace(display(nearType) + ": " + display(farType));
        }
        try {
            facetsGetter = BridgeFacet.class.getMethod("getBridgeFacets");
            facetsChecker = BridgeFacet.class.getMethod("hasBridgeFacets");
        } catch (Throwable throwable) {
            throw ExceptionWrapper.wrap(throwable);
        }
    }

    /**
     * Returns the near type of the bridge.
     * @return The near type
     */
    public Class<?> getNearType() {
        return nearType;
    }

    /**
     * Returns the far type of the bridge.
     * @return The far type
     */
    public Class<?> getFarType() {
        return farType;
    }

    protected static Class<?> getNearType(Class<?> farType) {
        Class<?> result;
        try {
            Method typeGetter = farType.getMethod("declareNearType");
            result = typeGetter.getReturnType();
        } catch (Throwable throwable) {
            throw ExceptionWrapper.wrap(throwable);
        }
        return result;
    }

    /**
     * Sets the {@link org.leialearns.bridge.BridgeHeadTypeRegistry BridgeHeadTypeRegistry}, registers this factory
     * instance with it, and looks up bindings for the methods in the near type.
     * @param registry The BridgeHeadTypeRegistry instance to use
     */
    @Autowired
    public void setRegistry(BridgeHeadTypeRegistry registry) {
        this.registry = registry;
        registry.register(this);
        Method[] methods = offer(Object.class.getMethods(), nearType.getMethods());
        for (Method method : methods) {
            if (logger.isTraceEnabled()) {
                logger.trace("Method: " + display(method) + ": in: " + display(nearType));
            }
            Binding binding = getBinding(method.getReturnType(), method.getName(), method.getParameterTypes());
            if (binding == null) {
                String message = "No binding found: " + display(nearType) + ": " + display(method);
                logger.warn(message);
                throw new UnsupportedOperationException(message);
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Binding: " + display(binding));
            }
            methodMap.put(method, binding);
        }
    }

    /**
     * Finds or creates a near object for the given far object.
     * @param farObject The far object that is to be wrapped by the near object
     * @return The near object that corresponds to the given far object
     */
    public Object getNearObject(Object farObject) {
        Object result = null;
        if (farObject instanceof BridgeFacet) {
            BridgeFacet facet = (BridgeFacet) farObject;
            if (facet.hasBridgeFacets()) {
                BridgeFacets facets = facet.getBridgeFacets();
                if (facets.hasNearObject()) {
                    result = nearType.cast(facets.getNearObject());
                }
            }
        }
        if (result == null) {
            if (!farType.isInstance(farObject)) {
                RuntimeException exception = new ClassCastException("Class: " + display(farType) + ": object: " + display(farObject));
                if (logger.isTraceEnabled()) {
                    logger.trace("Far type: " + display(farType), exception);
                }
                throw exception;
            }
            Class<?>[] interfaces = new Class[] { BridgeFacet.class };
            final BridgeFacets facets;
            if (farObject instanceof BaseBridgeFacet && ((BaseBridgeFacet) farObject).hasBridgeFacets()) {
                facets = ((BaseBridgeFacet) farObject).getBridgeFacets();
            } else {
                Map<Integer,Object> helperInstances = new HashMap<>();
                for (int i = 0; i < helpers.length; i++) {
                    Object helper = helpers[i];
                    if (helper instanceof Class) {
                        Class<?> helperType = (Class<?>) helper;
                        try {
                            Object helperInstance = helperType.newInstance();
                            helperInstances.put(i, helperInstance);
                        } catch (Throwable throwable) {
                            throw ExceptionWrapper.wrap(throwable);
                        }
                    }
                }
                facets = new BridgeFacets(farType.cast(farObject), this, helperInstances);
                for (Object helperInstance : helperInstances.values()) {
                    if (helperInstance instanceof BaseBridgeFacet) {
                        ((BaseBridgeFacet) helperInstance).setBridgeFacets(facets);
                    }
                }
            }
            if (farObject instanceof BaseBridgeFacet && !((BaseBridgeFacet) farObject).hasBridgeFacets()) {
                ((BaseBridgeFacet) farObject).setBridgeFacets(facets);
            }
            result = newProxyInstance(getClass().getClassLoader(), nearType, interfaces, new InvocationHandler() {
                @Override
                public Object invoke(Object o, Method method, Object[] parameters) throws Throwable {
                    Object result;
                    if (logger.isTraceEnabled()) {
                        logger.trace("Facets getter: " + display(facetsGetter));
                    }
                    if (method.equals(facetsGetter)) {
                        result = facets;
                    } else if (method.equals(facetsChecker)) {
                        result = Boolean.TRUE;
                    } else {
                        Binding binding = methodMap.get(method);
                        if (binding == null) {
                            String message = "No binding found: " + display(nearType) + ": " + display(method);
                            logger.warn(message);
                            throw new UnsupportedOperationException(message);
                        }
                        result = binding.invoke(facets, parameters);
                        Class<?> returnType = method.getReturnType();
                        if (logger.isTraceEnabled()) {
                            logger.trace("Return type: " + displayWithTypes(returnType));
                        }
                        if (result instanceof TypedIterable) {
                            TypedIterable<?> typedIterable = (TypedIterable<?>) result;
                            Class<?> baseType = typedIterable.getType();
                            if (logger.isTraceEnabled()) {
                                logger.trace("Iterable base type: " + display(baseType));
                            }
                            if (registry.hasFarType(baseType)) {
                                Class<?> type = getNearType(baseType);
                                result = getAdaptedIterable(returnType, typedIterable, type);
                            } else if (registry.hasNearType(baseType)) {
                                result = getAdaptedIterable(returnType, typedIterable, baseType);
                            } else if (logger.isTraceEnabled()) {
                                logger.trace("Iterable base type not found in registry");
                            }
                        } else if (registry.hasNearType(returnType)) {
                            Class<?> farType = registry.getFarType(method.getReturnType());
                            if (logger.isTraceEnabled()) {
                                logger.trace("Far type of return type: " + display(farType));
                            }
                            if (farType.isInstance(result)) {
                                BridgeFactory factory = registry.getBridgeFactory(farType);
                                if (logger.isTraceEnabled()) {
                                    logger.trace("Far type of return type factory: " + display(factory.getFarType()));
                                    logger.trace("Result: " + display(result));
                                    logger.trace("Return type: " + display(returnType) + ": factory PO type: " + display(factory.getNearType()));
                                }
                                try {
                                    result = factory.getNearObject(result);
                                } catch (ClassCastException exception) {
                                    if (logger.isTraceEnabled()) {
                                        logger.trace("Stack trace", exception);
                                    }
                                    throw exception;
                                }
                            }
                        } else if (logger.isTraceEnabled()) {
                            logger.trace("Not adapted");
                        }
                    }
                    return result;
                }
            });
            facets.setNearObject(result);
        }
        return result;
    }

    protected Object getAdaptedIterable(Class<?> returnType, TypedIterable<?> typedIterable, Class<?> type) {
        Class<?> baseType = typedIterable.getType();
        if (DeclaresNearType.class.isAssignableFrom(returnType)) {
            Method declaration;
            try {
                declaration = returnType.getDeclaredMethod("declareNearType");
            } catch (NoSuchMethodException exception) {
                throw ExceptionWrapper.wrap(exception);
            }
            Class<?> declaredType = declaration.getReturnType();
            if (!declaredType.isAssignableFrom(type)) {
                throw new ClassCastException("Near object list: " + display(baseType) + " # " + display(declaredType));
            } else if (logger.isTraceEnabled()) {
                logger.trace("Mapped list: " + display(baseType) + " -> " + display(declaredType));
            }
        } else if (logger.isTraceEnabled()) {
            logger.trace("Return type does not declare near type: " + display(returnType));
        }
        Object result = null;
        if (returnType.isInterface() && NearIterable.class.isAssignableFrom(returnType)) {
            if (logger.isTraceEnabled()) {
                logger.trace("Return near object iterable");
            }
            result = getNearObjectList(typedIterable, returnType, type, baseType);
        } else if (TypedIterable.class.isAssignableFrom(returnType)) {
            if (logger.isTraceEnabled()) {
                logger.trace("Return typed iterable that casts items on-the-fly");
            }
            result = getTypedIterable(typedIterable, type, baseType);
        }
        if (result == null) {
            result = typedIterable;
        }
        return result;
    }

    protected <NT> Object getNearObjectList(final TypedIterable<?> typedIterable, Class<?> returnType, final Class<NT> type, Class<?> baseType) {
        Class<?> interfaces[] = new Class<?>[] { returnType, HasWrappedIterable.class };
        final TypedIterable delegate = getTypedIterable(typedIterable, type, baseType);
        Method method;
        try {
            method = HasWrappedIterable.class.getDeclaredMethod("getWrappedIterable");
        } catch (NoSuchMethodException exception) {
            logger.warn("Method 'getWrappedIterable' not found", exception);
            method = null;
        }
        final Method wrappedGetter = method;
        return Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new InvocationHandler() {
            @Override
            public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
                Method delegateMethod = null;
                if (objects == null || objects.length < 1) {
                    try {
                        delegateMethod = delegate.getClass().getMethod(method.getName());
                    } catch (NoSuchMethodException exception) {
                        // Ignore
                    }
                }
                if (delegateMethod == wrappedGetter) {
                    return typedIterable;
                }
                if (delegateMethod == null) {
                    throw new UnsupportedOperationException(display(method));
                }

                return delegateMethod.invoke(delegate);
            }
        });
    }

    protected  TypedIterable<?> getTypedIterable(TypedIterable<?> typedIterable, final Class<?> type, Class<?> baseType) {
        TypedIterable<?> result;
        if (registry.hasNearType(baseType)) {
            result = typedIterable;
        } else {
            result = getBridgedTypedIterable(typedIterable, type, baseType);
        }
        return result;
    }

    protected  <NT> TypedIterable<NT> getBridgedTypedIterable(TypedIterable<?> typedIterable, final Class<NT> type, Class<?> baseType) {
        final BridgeFactory factory = registry.getBridgeFactory(baseType);
        return new BaseNearIterable<>(typedIterable, type, new Function<Object, NT>(){
            public NT get(Object x) {
                return type.cast(factory.getNearObject(x));
            }
        });
    }

    /**
     * Returns the array of helpers used by this factory instance
     * @return The array of helpers used by this factory instance
     */
    @SuppressWarnings("unused")
    public Object[] getHelpers() {
        return helpers.clone();
    }

    /**
     * Returns a helper of the given type. If more than one helper is an instance of the type, then the one that
     * occurs first in the helper array is returned. An {@link java.lang.IllegalStateException IllegalStateException}
     * is thrown when no helper is found.
     *
     * @param type The type of the requested helper
     * @return The requested helper
     */
    @SuppressWarnings("unused")
    public <T> T getHelper(Class<T> type) {
        T result = null;
        for (Object candidate : helpers) {
            if (type.isInstance(candidate)) {
                result = type.cast(candidate);
                break;
            }
        }
        if (result == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("Near type: " + display(nearType));
                for (Object helper : helpers) {
                    logger.trace("Helper type: " + display(helper.getClass()));
                }
            }
            logger.warn("No helper found: Requested type: " + display(type));
            throw new IllegalStateException("No helper found of type: " + type.getSimpleName());
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Helper found: " + display(result));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected <I> I newProxyInstance(ClassLoader classLoader, Class<I> type, Class<?>[] otherTypes, InvocationHandler invocationHandler) {
        Class<?>[] interfaces = offer(type, otherTypes);
        if (logger.isTraceEnabled()) {
            logger.trace(display(interfaces));
        }
        return (I) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }

    protected Binding getBinding(Class<?> returnType, String name, Class<?>[] parameterTypes) {
        Signature signature = new Signature(returnType, name, parameterTypes);
        Binding result;
        result = null;
        for (int i = 0; i < helpers.length; i++) {
            Object helper = helpers[i];
            if (helper == null) {
                result = findBinding(farType, -1, signature, null);
            } else if (helper instanceof Class) {
                result = findBinding((Class<?>) helper, i, signature, null);
            } else {
                result = findBinding(helper.getClass(), i, signature, nearType);
                if (result != null) {
                    break;
                }
                result = findBinding(helper.getClass(), i, signature, farType);
            }
            if (result != null) {
                break;
            }
        }
        if (result == null) {
            result = findBinding(farType, -1, signature, null);
        }
        if (result == null) {
            result = findBinding(Object.class, -1, signature, null);
        }
        return result;
    }
    protected Binding findBinding(Class<?> type, int delegationIndex, Signature signature, Class<?> targetType) {
        if (logger.isTraceEnabled()) {
            logger.trace("Helper class: " + displayWithTypes(type) + ": " + display(targetType));
        }
        Class<?>[] actualParameterTypes;
        if (targetType == null) {
            actualParameterTypes = signature.parameterTypes;
        } else {
            actualParameterTypes = signature.getBoundParameterTypes(targetType);
        }
        if (logger.isTraceEnabled()) {
            logger.trace(signature.name + "(" + display(actualParameterTypes) + ")");
        }
        Method result = null;
        List<BridgeAdapter> adapters = null;
        for (Method method : type.getMethods()) {
            if (!method.getName().equals(signature.name)) {
                continue;
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Candidate: " + display(method.getDeclaringClass()) + "." + method.getName() + "(" + display(method.getParameterTypes()) + ") -> " + display(method.getReturnType()));
            }
            Class<?>[] methodParameterTypes = method.getParameterTypes();
            List<BridgeAdapter> candidateAdapters = canBeAdaptedTo(methodParameterTypes, actualParameterTypes);
            if (candidateAdapters != null) {
                if (result == null || canBeAdaptedTo(result.getParameterTypes(), methodParameterTypes) != null) {
                    result = method;
                    adapters = candidateAdapters;
                }
            }
        }
        boolean useDTO = targetType != null && FarObject.class.isAssignableFrom(targetType);
        if (logger.isTraceEnabled()) {
            logger.trace(display(targetType) + ": useDTO: " + useDTO);
        }
        return (result == null ? null : new Binding(delegationIndex, useDTO, adapters, result));
    }

    protected List<BridgeAdapter> canBeAdaptedTo(Class<?>[] formalParameterTypes, Class<?>[] actualParameterTypes) {
        if (logger.isTraceEnabled()) {
            logger.trace(display(actualParameterTypes) + " -> " + display(formalParameterTypes));
        }
        List<BridgeAdapter> adapters;
        if (actualParameterTypes.length == formalParameterTypes.length) {
            adapters = new ArrayList<>();
            for (int i = 0; i < formalParameterTypes.length; i++) {
                Class<?> formalParameterType = formalParameterTypes[i];
                Class<?> actualParameterType = actualParameterTypes[i];
                if (logger.isTraceEnabled()) {
                    logger.trace(display(formalParameterType) + " <=? " + display(actualParameterType));
                }
                if (!canBeAdaptedTo(formalParameterType, actualParameterType, adapters, i)) {
                    adapters = null;
                    break;
                }
            }
        } else {
            adapters = null;
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Adapter List: " + adapters);
        }
        return adapters;
    }

    protected boolean canBeAdaptedTo(Class<?> formalParameterType, Class<?> actualParameterType, Collection<BridgeAdapter> adapters, int i) {
        boolean result = false;
        if (actualParameterType == null) {
            result = true;
        } else if (formalParameterType.isAssignableFrom(actualParameterType)) {
            if (TypedIterable.class.isAssignableFrom(actualParameterType)) {
                adapters.add(new TypedIterableAdapter(i));
            }
            result = true;
        } else if (Iterable.class.isAssignableFrom(formalParameterType) && NearIterable.class.isAssignableFrom(actualParameterType)) {
            adapters.add(new BridgeIterableAdapter(i, actualParameterType));
            result = true;
        } else if (FarObject.class.isAssignableFrom(formalParameterType)) {
            Class<?> nearType = getNearType(formalParameterType);
            if (nearType.isAssignableFrom(actualParameterType)) {
                adapters.add(new BridgeScalableAdapter(i, formalParameterType));
                result = true;
            }
        }
        return result;
    }

    protected interface BridgeAdapter {
        Object adapt(Object nearObject);
        int getIndex();
    }

    protected class BridgeScalableAdapter implements BridgeAdapter {
        private final int index;
        private final Class<?> adapterFarType;

        protected BridgeScalableAdapter(int index, Class<?> adapterFarType) {
            this.index = index;
            this.adapterFarType = adapterFarType;
        }

        @Override
        public Object adapt(Object nearObject) {
            return getFarObject(nearObject);
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return displayParts(getClass().getSimpleName(), adapterFarType.getSimpleName(), index);
        }
    }

    protected class TypedIterableAdapter implements BridgeAdapter {
        private final int index;

        protected TypedIterableAdapter(int index) {
            this.index = index;
        }

        public Object adapt(Object nearObject) {
            Object result;
            TypedIterable<?> iterable = (TypedIterable<?>) nearObject;
            if (registry.hasNearType(iterable.getType())) {
                Class<?> farType = registry.getFarType(iterable.getType());
                result = adaptIterable(iterable, farType);
            } else {
                result = nearObject;
            }
            return result;
        }

        protected <NT, FT> TypedIterable<FT> adaptIterable(TypedIterable<NT> iterable, final Class<FT> farType) {
            return new TransformingIterable<>(
                    iterable,
                    farType,
                    new Function<Object, FT>() {
                        @Override
                        public FT get(Object x) {
                            return farType.cast(getFarObject(x));
                        }
                    }
            );
        }

        @Override
        public int getIndex() {
            return index;
        }
    }


    protected class BridgeIterableAdapter implements BridgeAdapter {
        private final int index;
        private final Class<?> adapterNearType;
        private final Setting<Class<?>> adapterFarType = new Setting<>("Iterable adapter far type", new Expression<Class<?>>() {
            @Override
            public Class<?> get() {
                return registry.getFarType(adapterNearType);
            }
        });

        protected BridgeIterableAdapter(int index, Class<?> iteratorType) {
            if (!NearIterable.class.isAssignableFrom(iteratorType)) {
                throw new IllegalArgumentException("Should implement NearIterable: " + display(iteratorType));
            }
            Method typeDeclaration;
            try {
                typeDeclaration = iteratorType.getDeclaredMethod("declareNearType");
            } catch (NoSuchMethodException exception) {
                throw ExceptionWrapper.wrap(exception);
            }
            this.adapterNearType = typeDeclaration.getReturnType();
            this.index = index;
        }

        @Override
        public Object adapt(Object nearObject) {
            Object result = null;
            if (nearObject instanceof HasWrappedIterable) {
                Iterable<?> wrappedIterable = ((HasWrappedIterable) nearObject).getWrappedIterable();
                if (wrappedIterable instanceof TypedIterable<?>) {
                    TypedIterable<?> typedIterable = (TypedIterable<?>) wrappedIterable;
                    if (adapterFarType.get().isAssignableFrom(typedIterable.getType())) {
                        result = wrappedIterable;
                    }
                }
            }
            if (result == null) {
                Iterable<?> iterable = (Iterable<?>) nearObject;
                result = getTransformingIterable(iterable, adapterFarType.get());
            }
            logger.debug("Adapted: {} -> {}", asDisplayWithTypes(getType(nearObject)), asDisplayWithTypes(getType(result)));
            return result;
        }

        protected <FT> TransformingIterable<FT> getTransformingIterable(Iterable<?> iterable, final Class<FT> farType) {
            return new TransformingIterable<>(iterable, farType, new Function<Object, FT>() {
                @Override
                public FT get(Object x) {
                    return farType.cast(getFarObject(x));
                }
            });
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return displayParts(getClass().getSimpleName(), adapterNearType.getSimpleName(), index);
        }
    }

    @SuppressWarnings("unchecked")
    protected static Class<? extends FarObject<?>> getFarType(String farTypeName) {
        Class<?> result;
        try {
            result = Class.forName(farTypeName);
        } catch (Throwable throwable) {
            throw ExceptionWrapper.wrap(throwable);
        }
        if (!FarObject.class.isAssignableFrom(result)) {
            throw new IllegalArgumentException("Does not extend FarObject: [" + farTypeName + "]");
        }
        return (Class<? extends FarObject<?>>) result;
    }

    protected static Class<?> getType(Object object) {
        return object == null ? null : object.getClass();
    }

    protected static class Signature {
        private final Class<?> returnType;
        private final String name;
        private final Class<?>[] parameterTypes;

        public Signature(Class<?> returnType, String name, Class<?>[] parameterTypes) {
            this.returnType = returnType;
            this.name = name;
            for (Class<?> parameterType : parameterTypes) {
                if (parameterType == null) {
                    throw new IllegalArgumentException("Parameter type may not be null: " + display(parameterTypes));
                }
            }
            this.parameterTypes = parameterTypes;
        }

        protected Class<?>[] getBoundParameterTypes(Class<?> type) {
            return offer(type, parameterTypes);
        }

        @Override
        public int hashCode() {
            int result = returnType.hashCode() + name.hashCode();
            for (Class<?> parameterType : parameterTypes) {
                result += parameterType.hashCode();
            }
            return result;
        }

        @Override
        public boolean equals(Object other) {
            boolean result;
            if (other instanceof Signature) {
                Signature otherSignature = Signature.class.cast(other);
                result = otherSignature.returnType.equals(returnType);
                result = result && otherSignature.name.equals(name) && otherSignature.parameterTypes.length == parameterTypes.length;
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (!result) {
                        break;
                    }
                    result = otherSignature.parameterTypes[i] == parameterTypes[i];
                }
            } else {
                result = false;
            }
            return result;
        }
    }

    protected class Binding {
        private final int delegationIndex;
        private final boolean useDTO;
        private final List<BridgeAdapter> adapters;
        private final Method method;

        public Binding(int delegationIndex, boolean useDTO, List<BridgeAdapter> adapters, Method method) {
            this.delegationIndex = delegationIndex;
            this.useDTO = useDTO;
            this.adapters = adapters;
            this.method = method;
            if (logger.isTraceEnabled()) {
                logger.trace(delegationIndex + ": " + useDTO + ": " + display(method));
            }
        }

        public Object invoke(BridgeFacets facets, Object[] parameters) {
            Object[] adapted = (parameters == null ? new Object[0] : parameters.clone());
            Object targetObject;
            if (delegationIndex < 0) {
                targetObject = facets.getFarObject();
            } else if (helpers[delegationIndex] instanceof Class) {
                targetObject = facets.getHelper(delegationIndex);
            } else {
                targetObject = helpers[delegationIndex];
                Object facet;
                if (useDTO) {
                    facet = facets.getFarObject();
                } else {
                    facet = facets.getNearObject();
                }
                if (logger.isTraceEnabled()) {
                    logger.trace(display(targetObject) + "." + method.getName() + "(" + display(facet) + "...");
                }
                adapted = offer(facet, adapted);
            }
            adapted = adapt(adapted);
            if (logger.isTraceEnabled()) {
                // logger.trace("Declaring class of method: " + display(method.getDeclaringClass()));
                List<String> messages = new ArrayList<>();
                messages.add(displayWithTypes(targetObject) + "." + method.getName() + (adapted.length > 0 ? "(" : "()"));
                if (adapted.length > 0) {
                    for (Object object : adapted) {
                        messages.add("  " + displayWithTypes(object));
                    }
                    messages.add(")");
                }
                for (String message : messages) {
                    logger.trace(message);
                }
                logger.trace("Method: " + display(method));
            }
            Object result;
            try {
                result = method.invoke(targetObject, adapted);
            } catch (InvocationTargetException exception) {
                throw ExceptionWrapper.wrap(exception.getTargetException());
            } catch (Throwable exception) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Exception", exception);
                }
                throw ExceptionWrapper.wrap(exception);
            }
            return result;
        }

        protected Object[] adapt(Object[] parameters) {
            Object[] result;
            if (parameters == null) {
                result = new Object[0];
            } else if (adapters.isEmpty()) {
                result = parameters;
            } else {
                result = parameters; // .clone()
                for (BridgeAdapter adapter : adapters) {
                    int index = adapter.getIndex();
                    if (result.length > index) {
                        result[index] = adapter.adapt(result[index]);
                    }
                }
            }
            return result;
        }
    }

}
