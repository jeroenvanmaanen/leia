package org.leialearns.logic.utilities;

import org.leialearns.logic.model.TypedVersionExtension;
import org.leialearns.logic.model.Version;
import org.leialearns.utilities.Expression;
import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Static {
    private static final Setting<Logger> logger = new Setting<>("Logger", new Expression<Logger>() {
        @Override
        public Logger get() {
            return LoggerFactory.getLogger(Static.class);
        }
    });

    public static Long getVersionOrdinal(TypedVersionExtension extension) {
        return getVersionOrdinal(extension.getModelType().name(), extension);
    }

    public static Long getVersionOrdinal(String label, TypedVersionExtension extension) {
        String prefix = (label == null || label.length() < 1 ? "" : label + ": ");
        logger.get().trace(prefix + "Extension: " + extension);
        Long result = null;
        if (extension != null) {
            Version version = extension.getVersion();
            logger.get().trace(prefix + "Version: " + version);
            if (version != null) {
                result = version.getOrdinal();
            }
        }
        logger.get().trace(prefix + "ID: " + result);
        return result;
    }

}
