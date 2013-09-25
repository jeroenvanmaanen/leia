package org.leialearns.bridge;

import org.leialearns.utilities.Function;
import org.leialearns.utilities.TransformingIterable;

/**
 * Convenience class that can be used as a super class for classes that implement
 * {@link org.leialearns.bridge.NearIterable NearIterable}.
 * @param <NT> The type of objects returned by the iterator
 */
public class BaseNearIterable<NT> extends TransformingIterable<NT> implements NearIterable<NT> {

    /**
     * Creates a new <code>BaseNearIterable</code> instance.
     * @param iterable The iterable that backs this instance
     * @param type The type of object returned by the iterator
     * @param function The function that converts objects returned by the backing iterable
     */
    public BaseNearIterable(Iterable<?> iterable, Class<NT> type, Function<Object, NT> function) {
        super(iterable, type, function);
    }

    public NT declareNearType() {
        throw new UnsupportedOperationException("");
    }
}
