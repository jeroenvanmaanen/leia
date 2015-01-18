package org.leialearns.api.model;

import org.leialearns.api.enumerations.ModelType;

public interface TypedVersionExtension {
    Version getVersion();
    ModelType getModelType();
}
