package org.leialearns.api.model.common;

import java.util.function.Supplier;

public interface NodeData<ItemType> {
    String getTypeLabel();
    void setLocation(Supplier<String> locationSupplier);
    void log();
    void log(String label);
    void retrieve(Supplier<Iterable<ItemType>> getItems);
}
