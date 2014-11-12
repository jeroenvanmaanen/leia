package org.leialearns.logic.common;

import org.leialearns.api.model.TypedVersionExtension;
import org.leialearns.api.model.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Static {
    private static final Logger logger = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());

    public static Long getVersionOrdinal(TypedVersionExtension extension) {
        return getVersionOrdinal(extension.getModelType().name(), extension);
    }

    public static Long getVersionOrdinal(String label, TypedVersionExtension extension) {
        String prefix = (label == null || label.length() < 1 ? "" : label + ": ");
        logger.trace(prefix + "Extension: " + extension);
        Long result = null;
        if (extension != null) {
            Version version = extension.getVersion();
            logger.trace(prefix + "Version: " + version);
            if (version != null) {
                result = version.getOrdinal();
            }
        }
        logger.trace(prefix + "ID: " + result);
        return result;
    }
}
