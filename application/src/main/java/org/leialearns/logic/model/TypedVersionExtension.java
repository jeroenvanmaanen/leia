package org.leialearns.logic.model;

import org.leialearns.enumerations.ModelType;

public interface TypedVersionExtension {
    Version getVersion();
    ModelType getModelType();
}
