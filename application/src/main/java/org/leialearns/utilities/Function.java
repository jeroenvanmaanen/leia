package org.leialearns.utilities;

/**
 * Convenience interface for inline class instances that map values from the domain type to the range type.
 * @param <Domain> The type of the values that are to be mapped
 * @param <Range> The type of the returned mapped values
 */
public interface Function<Domain,Range> {

    /**
     * Return the mapped value for the given object
     * @param x The object to map
     * @return The mapped value
     */
    Range get(Domain x);
}
