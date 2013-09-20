package org.leialearns.utilities;

/**
 * Convenience interface for inline class instances that provide a single value.
 * @param <T> The type of the value
 */
public interface Expression<T> {

    /**
     * Returns the value of this expression.
     * @return The value of this expression
     */
    public T get();

}
