package org.leialearns.utilities;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides static convenience methods.
 */
public class Static {

    private Static() {
        throw new UnsupportedOperationException("This class must not be instantiated: " + getClass().getSimpleName());
    }

    /**
     * Creates a copy of an array with a single item prepended to it.
     * @param head The item to prepend
     * @param tail The original array
     * @param <T> The base type of the array
     * @return The extended array
     */
    public static <T> T[] offer(T head, T[] tail) {
        T[] result = newArrayInstance(tail, tail.length + 1);
        result [0] = head;
        System.arraycopy(tail, 0, result, 1, tail.length);
        return result;
    }

    /**
     * Creates a copy of an array with another array prepended to it.
     * @param prepend The array to prepend
     * @param tail The original array
     * @param <T> The base type of the array
     * @return The extended array
     */
    public static <T> T[] offer(T[] prepend, T[] tail) {
        T[] result = newArrayInstance(tail, prepend.length + tail.length);
        System.arraycopy(prepend, 0, result, 0, prepend.length);
        System.arraycopy(tail, 0, result, prepend.length, tail.length);
        return result;
    }

    /**
     * Creates a new array instance in a type safe way.
     * @param template The original array
     * @param length The length of the new array
     * @param <T> The base type of the new array
     * @return The new array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArrayInstance(T[] template, int length) {
        return (T[]) Array.newInstance(template.getClass().getComponentType(), length);
    }

    /**
     * Compares two objects. This method is careful about <code>null</code> values.
     * @param thisObject One object
     * @param thatObject Other object
     * @return <code>true</code> if the one object equals the other object; <code>false</code> otherwise
     */
    public static boolean equal(Object thisObject, Object thatObject) {
        return thisObject == null ? thatObject == null : thisObject.equals(thatObject);
    }

    /**
     * Returns the class that can be used to derive the logger name from.
     * For CGLIB advised classes this is the super class of the object class.
     * @param object The object to query
     * @return The logging class of the object
     */
    public static Class<?> getLoggingClass(Object object) {
        Class<?> result = object.getClass();
        if (result.getSimpleName().matches(".*[$].*CGLIB.*")) {
            result = result.getSuperclass();
        }
        return result;
    }

    /**
     * Joins an array of strings together using a given separator
     * @param separator The separator to use
     * @param parts An array of strings
     * @return The concatenation of the parts with separators in between
     */
    public static String join(String separator, String[] parts) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String part : parts) {
            if (first) {
                first = false;
            } else {
                builder.append(separator);
            }
            builder.append(part);
        }
        return builder.toString();
    }

    public static <T> List<T> toList(TypedIterable<T> iterable) {
        List<T> result = new ArrayList<>();
        for (T item : iterable) {
            result.add(item);
        }
        return result;
    }

    public static long gcd(long a, long b) {
        BigInteger aa = BigInteger.valueOf(a);
        BigInteger bb = BigInteger.valueOf(b);
        return aa.gcd(bb).longValue();
    }

}
