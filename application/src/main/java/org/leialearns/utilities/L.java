package org.leialearns.utilities;

import org.leialearns.common.Display;

/**
 * Provides a wrapped String that is not mangled by {@link Display#show(String)}.
 */
public class L {
    private final String literal;

    protected L(String literal) {
        this.literal = literal;
    }

    /**
     * Creates a new <code>L</code> instance.
     * @param literal The literal to wrap
     * @return The wrapped literal
     */
    public static L literal(String literal) {
        return new L(literal);
    }

    public String toString() {
        return literal;
    }

}
