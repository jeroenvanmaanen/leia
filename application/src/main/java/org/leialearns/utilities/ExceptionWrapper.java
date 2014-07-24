package org.leialearns.utilities;

/**
 * <p>Provides a static function to wrap a throwable in a RuntimeException if necessary.</p>
 *
 * <p>This can be used instead of declaring checked exceptions, like this:</p>
 * <code>
 * <pre>
 *     try {
 *         f = open("/dev/null", "w");
 *     } catch (Throwable throwable) {
 *         throw ExceptionWrapper.wrap(throwable);
 *     }
 * </pre>
 * </code>
 */
public class ExceptionWrapper {

    private ExceptionWrapper() {
        throw new UnsupportedOperationException("This class must not be instantiated: " + getClass().getSimpleName());
    }

    /**
     * Returns the given throwable as a <code>RuntimeException</code> wrapping it in a new
     * <code>RuntimeException</code> if necessary.
     * @param throwable The throwable to wrap
     * @return The wrapped throwable
     */
    public static RuntimeException wrap(Throwable throwable) {
        RuntimeException result;
        if (throwable instanceof RuntimeException) {
            result = (RuntimeException) throwable;
        } else {
            result = new RuntimeException(throwable.getClass().getSimpleName(), throwable);
        }
        return result;
    }

}
