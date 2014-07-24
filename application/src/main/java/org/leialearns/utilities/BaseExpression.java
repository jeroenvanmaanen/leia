package org.leialearns.utilities;

public abstract class BaseExpression<T> implements Expression<T> {
    public String toString() {
        return String.valueOf(get());
    }
}
