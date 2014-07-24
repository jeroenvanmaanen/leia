package org.leialearns.utilities;

import java.util.Iterator;

/**
 * Adds a bit of run-time type information to an iterable.
 * @param <T> The base type of the iterable
 */
public class TypedIterable<T> implements Iterable<T>, HasWrappedIterable {
    private final Iterable<T> iterable;
    private final Class<T> type;

    /**
     * Creates a new <code>TypedIterable</code> instance.
     * @param iterable The backing iterable
     * @param type The base type of the iterable
     */
    public TypedIterable(Iterable<T> iterable, Class<T> type) {
        this.iterable = iterable;
        this.type = type;
    }

    /**
     * Creates a new <code>TypedIterable</code> instance. Performs a cast on each item to make sure that the elements
     * to the backing iterable conform to the specified type.
     * @param type The base type of the iterable
     * @param iterable The backing iterable
     */
    public TypedIterable(Class<T> type, Iterable<?> iterable) {
        this(cast(iterable, type), type);
    }

    /**
     * Returns an iterator for this iterable.
     * @return An iterator for this iterable
     */
    public Iterator<T> iterator() {
        return iterable.iterator();
    }

    /**
     * Returns the base type of this iterable.
     * @return The base type of this iterable
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Returns the backing iterable.
     * @return The backing iterable
     */
    public Iterable<?> getWrappedIterable() {
        return iterable;
    }

    /**
     * Returns the first item of this iterable.
     * @return The first item of this iterable
     */
    public T first() {
        Iterator<T> iterator = iterator();
        return (iterator.hasNext() ? iterator.next() : null);
    }

    /**
     * Returns a flag that indicates whether this iterable is empty.
     * @return <code>true</code> if this iterable contains no elements; <code>false</code> otherwise
     */
    public boolean isEmpty() {
        return !iterator().hasNext();
    }

    @SuppressWarnings("unchecked")
    private static <T> Iterable<T> cast(Iterable<?> iterable, Class<T> type) {
        for (Object object : iterable) {
            type.cast(object);
        }
        return (Iterable<T>) iterable;
    }

}
