package org.leialearns.api.enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the possible values of the <code>modelType</code> property of a {@link org.leialearns.api.model.Version}
 */
public enum ModelType {

    /**
     * Indicates a counted version that links {@link org.leialearns.api.model.histogram.Counter}s to
     * &lt;{@link org.leialearns.api.structure.Node},{@link org.leialearns.api.interaction.Symbol}&gt;-pairs
     */
    COUNTED,

    /**
     * Indicates a version of the model of observed behavior that links {@link org.leialearns.api.model.histogram.Histogram}s to
     * {@link org.leialearns.api.structure.Node}s and has an associated delta version as well.
     */
    OBSERVED,

    /**
     * Indicates a version of delta's that specifies the amount of data that is covered by deeper nodes that are
     * included in the model of expected behavior.
     */
    DELTA,

    /**
     * Indicates a version that specifies a {@link org.leialearns.api.structure.Node} to be either included in or excluded
     * from the model of expected behavior.
     */
    TOGGLED,

    /**
     * Indicates a consolidated version of the model of expected behavior.
     */
    EXPECTED,

    /**
     * Indicates a version of a policy that can be used by an encounter to interact with the outside world.
     */
    POLICY;

    private static final Map<Character,ModelType> CHARACTER_MODEL_TYPE_MAP = new HashMap<>();
    private final char typeChar;

    static {
        for (ModelType modelType : values()) {
            CHARACTER_MODEL_TYPE_MAP.put(modelType.typeChar, modelType);
        }
    }

    ModelType() {
        typeChar = this.name().charAt(0);
    }

    @SuppressWarnings("unused")
    ModelType(char typeChar) {
        this.typeChar = typeChar;
    }

    /**
     * Returns the single character representation of this model type.
     * @return The single character representation of this model type
     */
    public char toChar() {
        return typeChar;
    }

    /**
     * Returns the model type constant that corresponds to the given character.
     * @param typeChar The single character representation of the requested model type
     * @return The model type constant that corresponds to the given character
     */
    public static ModelType valueOf(char typeChar) {
        return CHARACTER_MODEL_TYPE_MAP.get(typeChar);
    }
}
