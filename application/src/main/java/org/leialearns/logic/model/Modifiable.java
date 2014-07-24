package org.leialearns.logic.model;

public interface Modifiable {
    void add(Histogram other);
    void subtract(Histogram other);
}
