package org.leialearns.logic.utilities;

import org.leialearns.logic.model.TypedVersionExtension;
import org.leialearns.logic.model.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

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

    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> notNull(Iterable<T> iterable) {
        return iterable == null ? Collections.EMPTY_LIST : iterable;
    }

    public static <T extends Comparable<T>> int compare(T left, T right) {
        int result;
        if (left == null) {
            if (right == null) {
                result = 0;
            } else {
                result = -1;
            }
        } else {
            if (right == null) {
                result = 1;
            } else {
                result = left.compareTo(right);
            }
        }
        return result;
    }
}
