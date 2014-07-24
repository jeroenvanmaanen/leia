package org.leialearns.enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the possible values of the <code>accessMode</code> property of a {@link org.leialearns.logic.model.Version}.
 */
public enum AccessMode {

    /**
     * Indicates that a session is trying to get a lock.
     */
    LOCKING('l'),

    /**
     * Indicates that one of the sessions holds a lock.
     */
    LOCKED,

    /**
     * Indicates that multiple sessions can read this version.
     */
    READABLE,

    /**
     * Indicates that multiple sessions can write this version.
     */
    WRITABLE,

    /**
     * Indicates that this version should be ignored completely.
     */
    EXCLUDE('X');

    private static final Map<Character,AccessMode> CHARACTER_ACCESS_MODE_MAP = new HashMap<Character, AccessMode>();
    private final char typeChar;

    static {
        for (AccessMode accessMode : values()) {
            CHARACTER_ACCESS_MODE_MAP.put(accessMode.typeChar, accessMode);
        }
    }

    AccessMode() {
        typeChar = this.name().charAt(0);
    }
    AccessMode(char typeChar) {
        this.typeChar = typeChar;
    }

    /**
     * Returns the single character representation of this access mode.
     * @return The single character representation of this access mode
     */
    public char toChar() {
        return typeChar;
    }

    /**
     * Returns the access mode constant that corresponds to the given character.
     * @param typeChar The single character representation of the requested access mode
     * @return The access mode constant that corresponds to the given character
     */
    public static AccessMode valueOf(char typeChar) {
        return CHARACTER_ACCESS_MODE_MAP.get(typeChar);
    }

}
