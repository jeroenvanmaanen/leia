package org.leialearns.enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the possible values for the <code>direction</code> property of a {@link org.leialearns.api.interaction.DirectedSymbol}.
 */
public enum Direction {

    /**
     * Indicates that the symbol was sent from <b>LEIA</b> to its environment.
     */
    ACTION('>'),

    /**
     * Indicates that the symbol was received by <b>LEIA</b> from its environment.
     */
    RESPONSE('<');

    private final char character;
    private static final Map<Character,Direction> CHARACTER_DIRECTION_MAP = new HashMap<>();

    static {
        for (Direction direction : Direction.values()) {
            CHARACTER_DIRECTION_MAP.put(direction.character, direction);
        }
    }

    Direction(char character) {
        this.character = character;
    }

    /**
     * Returns the -- unique -- character that corresponds to this direction. This can be used for compact
     * representations that specify directions.
     * @return The character that corresponds to this direction
     */
    public char toChar() {
        return character;
    }

    /**
     * Returns the direction constant that corresponds to the given character. This is the reverse operation of
     * {@link #toChar()}.
     * @param character The character value to look up
     * @return The corresponding direction constant
     * @throws java.lang.IllegalArgumentException If no direction constant corresponds to the given character
     */
    public static Direction valueOf(char character) {
        if (!CHARACTER_DIRECTION_MAP.containsKey(character)) {
            throw new IllegalArgumentException("There is no direction constant for character: [" + character + "]");
        }
        return CHARACTER_DIRECTION_MAP.get(character);
    }

}
