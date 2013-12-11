package org.leialearns.bridge;

import org.leialearns.utilities.ExceptionWrapper;
import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.getLoggingClass;

/**
 * Injects registered {@link BridgeFactory} beans into bean properties of type {@link FactoryAccessor}.
 */
public class FactoryInjector implements BeanPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Collection<FactoryAccessor<?>> accessors = new ArrayList<>();
    private final Setting<BridgeHeadTypeRegistry> registry = new Setting<>("Bridge Head Type Registry");

    public FactoryInjector() {
        logger.info("Create FactoryInjector: " + this);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        logger.debug("Begin post-process: " + display(bean.getClass()));
        if (bean instanceof Advised) {
            Advised advised = (Advised) bean;
            TargetSource targetSource = advised.getTargetSource();
            try {
                postProcessAfterInitialization(targetSource.getTarget(), beanName);
            } catch (Throwable throwable) {
                throw ExceptionWrapper.wrap(throwable);
            }
        }
        Class<?> type = bean.getClass();
        for (Field field : type.getFields()) {
            Class<?> fieldType = field.getType();
            logger.trace("Field: {}", field.getName());
            if (FactoryAccessor.class.isAssignableFrom(fieldType)) {
                try {
                    FactoryAccessor<?> accessor = (FactoryAccessor<?>) field.get(bean);
                    logger.debug("Factory accessor: " + field.getName() + ": " + accessor.getNearType().getSimpleName());
                    if (registry.isFixated()) {
                        registry.get().injectInto(accessor);
                    } else {
                        accessors.add(accessor);
                    }
                    accessors.add(accessor);
                } catch (IllegalAccessException exception) {
                    logger.debug("Can't inject factory", exception);
                }
            }
        }
        logger.debug("End post-process");
        return bean;
    }

    /**
     * Sets the registry that maps types to bridge factories.
     * @param registry The registry to use
     */
    public void setRegistry(BridgeHeadTypeRegistry registry) {
        this.registry.set(registry);
        for (FactoryAccessor<?> accessor : accessors) {
            registry.injectInto(accessor);
        }
    }

}
