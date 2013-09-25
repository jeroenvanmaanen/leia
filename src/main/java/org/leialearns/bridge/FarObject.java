package org.leialearns.bridge;

/**
 * Marker interface to signify that this type of object can be used as a far object in the context of a
 * bridge instance.
 * @param <NT> The corresponding near type
 */
public interface FarObject<NT> extends DeclaresNearType<NT> {
}
