package org.leialearns.utilities;

import javax.validation.constraints.NotNull;

import static org.leialearns.common.Display.displayParts;
import static org.leialearns.common.Static.equal;
import static org.leialearns.utilities.L.literal;

public class Pair<L extends Comparable<? super L>,R extends Comparable<? super R>> implements Comparable<Pair<L,R>> {
    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Pair && equal(left, ((Pair<?,?>) other).left) && equal(right, ((Pair<?,?>) other).right);
    }

    @Override
    public int hashCode() {
        return (left == null ? 0 : left.hashCode()) + (right == null ? 0 : right.hashCode());
    }

    @Override
    public String toString() {
        return displayParts("Pair", literal(left.toString()), literal(right.toString()));
    }

    @Override
    public int compareTo(@NotNull Pair<L,R> pair) {
        int result = compare(left, pair.left);
        return result == 0 ? compare(right, pair.right) : result;
    }

    protected <T extends Comparable<? super T>> int compare(T thisObject, T otherObject) {
        int result;
        if (thisObject == null) {
            result = otherObject == null ? 0 : -1;
        } else if (otherObject == null) {
            result = 1;
        } else {
            result = thisObject.compareTo(otherObject);
        }
        return result;
    }

}
