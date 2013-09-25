package org.leialearns.bridge;

/**
 * Provides an iterable with a few convenience methods.
 * @param <NT> The type of objects returned by the iterator
 */
public interface NearIterable<NT> extends DeclaresNearType<NT>, Iterable<NT> {
    /**
     * Returns the first object of this iterable; or <code>null</code> if the iterable is empty.
     * @return The first object of this iterable; or <code>null</code> if the iterable is empty
     */
    NT first();

    /**
     * Returns an flag that indicates whether the iterable is empty.
     * @return <code>true</code> if the iterable is empty; <code>false</code> otherwise
     */
    boolean isEmpty();
}
