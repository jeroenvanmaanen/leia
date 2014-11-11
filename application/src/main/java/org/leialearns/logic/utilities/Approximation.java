package org.leialearns.logic.utilities;

import org.leialearns.logic.model.expectation.Fraction;
import org.leialearns.logic.model.expectation.TransientFraction;
import org.leialearns.utilities.TransformingIterable;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkNotNull;

public class Approximation implements Comparable<Approximation>, Iterable<Approximation> {
    final Iterable<?> singleton = Collections.unmodifiableCollection(Arrays.asList(new Object()));

    private final Fraction fraction;
    private Approximation left = null;
    private Approximation right = null;
    private int depth = 0;

    protected Approximation(Fraction fraction) {
        this.fraction = checkNotNull(fraction);
    }

    public Fraction getFraction() {
        return fraction;
    }

    public long getIndex() {
        return fraction.getIndex();
    }

    public long getNumerator() {
        return fraction.getNumerator();
    }

    public long getDenominator() {
        return fraction.getDenominator();
    }

    public Approximation getLeft() {
        return left;
    }

    public Approximation getRight() {
        return right;
    }

    public void clear() {
        left = null;
        right = null;
    }

    public boolean insert(Approximation approximation) {
        boolean result;
        if (equals(approximation)) {
            result = false;
        } else if (compareTo(approximation) > 0) {
            if (left == null) {
                left = approximation;
                approximation.depth = depth + 1;
                result = true;
            } else {
                result = left.insert(approximation);
            }
        } else {
            if (right == null) {
                right = approximation;
                approximation.depth = depth + 1;
                result = true;
            } else {
                result = right.insert(approximation);
            }
        }
        return result;
    }

    public Approximation add(Approximation other) {
        long newNumerator = other.getDenominator() * getNumerator() + getDenominator() * other.getNumerator();
        long newDenominator = getDenominator() * other.getDenominator();
        return new Approximation(new TransientFraction(-1, newNumerator, newDenominator));
    }

    public Approximation multiply(Approximation other) {
        return new Approximation(new TransientFraction(-1, getNumerator() * other.getNumerator(), getDenominator() * other.getDenominator()));
    }

    @SuppressWarnings("unused")
    public Approximation invert() {
        return new Approximation(new TransientFraction(-1, getDenominator(), getNumerator()));
    }

    public Iterator<Approximation> iterator() {
        return createApproximationIterator(left, createSingleton(this), right);
    }

    public int compareTo(@NotNull Approximation other) {
        return Long.signum((getNumerator() * other.getDenominator()) - (other.getNumerator() * getDenominator()));
    }

    public boolean equals(Object other) {
        return other instanceof Approximation && compareTo((Approximation) other) == 0;
    }

    public int hashCode() {
        return (int) (getNumerator() * getDenominator());
    }

    public String toString() {
        return "[Approximation:" + getIndex() + "->" + getNumerator() + "/" + getDenominator() + "(" + depth + ")]";
    }

    protected Iterable<Approximation> createSingleton(final Approximation approximation) {
        return new TransformingIterable<>(singleton, Approximation.class, x -> approximation);
    }

    @SafeVarargs
    protected final Iterator<Approximation> createApproximationIterator(Iterable<Approximation>... iterables) {
        return new ApproximationIterator(iterables);
    }

    protected class ApproximationIterator implements Iterator<Approximation> {
        private final Iterator<Iterable<Approximation>> iterables;
        private Iterator<Approximation> current = null;

        @SafeVarargs
        protected ApproximationIterator(Iterable<Approximation>... iterables) {
            this.iterables = Arrays.asList(iterables).iterator();
            setCurrent();
        }

        @Override
        public boolean hasNext() {
            return current != null && current.hasNext();
        }

        @Override
        public Approximation next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            Approximation result = current.next();
            setCurrent();
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        protected void setCurrent() {
            while (current == null || !current.hasNext()) {
                current = null;
                if (!iterables.hasNext()) {
                    break;
                }
                Iterable<Approximation> iterable = iterables.next();
                if (iterable == null) {
                    continue;
                }
                current = iterable.iterator();
            }
        }
    }
}
