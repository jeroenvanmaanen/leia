package org.leialearns.utilities;

/**
 * Provides a method to get the original iterable that was wrapped by <em>i.e.</em>,
 * a <code>TransformingIterable</code>.
 */
public interface HasWrappedIterable {

    /**
     * Returns the wrapped iterable that backs this iterable.
     * @return The wrapped iterable that backs this iterable
     */
    Iterable<?> getWrappedIterable();

}
