package org.leialearns.logic.model.histogram;

public interface Modifiable {
    void add(Histogram other);
    void subtract(Histogram other);
}
