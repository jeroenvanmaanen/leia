package org.leialearns.bridge;

/**
 * Reminder interface to encourage implementers of {@link org.leialearns.bridge.FarObject FarObject}s and
 * {@link org.leialearns.bridge.NearIterable NearIterable}s to declare the corresponding near type.
 * @param <NT> The near type that corresponds to this object
 */
public interface DeclaresNearType<NT> {
    /**
     * Declares the near type that corresponds to the object that implements this interface. The return type of
     * this method can be obtained using reflection.
     * @throws java.lang.UnsupportedOperationException <strong>Always</strong>
     */
    NT declareNearType();
}
