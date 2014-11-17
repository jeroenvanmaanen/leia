package org.leialearns.utilities;

import java.util.function.Supplier;

public abstract class BaseSupplier<T> implements Supplier<T> {
    public String toString() {
        return String.valueOf(get());
    }
}
