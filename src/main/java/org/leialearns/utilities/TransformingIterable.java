package org.leialearns.utilities;

import java.util.Iterator;

/**
 * Produces iterators that transform each element returned by an iterator of a backing iterable.
 * @param <T> The type of the transformed elements
 */
public class TransformingIterable<T> extends TypedIterable<T> {

    /**
     * Creates a new <code>TransformingIterable</code> instance.
     * @param iterable The backing iterable
     * @param type The type of the transformed elements
     * @param function The function that is used to transform each element
     */
    public TransformingIterable(Iterable<?> iterable, Class<T> type, Function<Object, T> function) {
        super(getIterable(iterable, function), type);
    }

    protected static <T> Iterable<T> getIterable(final Iterable<?> iterable, final Function<Object, T> transformation) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                final Iterator<?> delegate = iterable.iterator();
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return delegate.hasNext();
                    }

                    @Override
                    public T next() {
                        return transformation.get(delegate.next());
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}
