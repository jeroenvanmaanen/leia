package org.leialearns.logic.utilities;

import java.math.BigInteger;

/**
 * Provides static functions to calculate the description length of a {@link java.math.BigInteger}. See
 * {@link HasDescriptionLength} for more information on the importance of description lengths in the context of
 * <b>LEIA</b>.
 */
public class DescriptionLength {

    private DescriptionLength() {
        throw new UnsupportedOperationException("This class must not be instantiated: " + getClass().getSimpleName());
    }

    /**
     * Returns an ascii form of the binary representation of the given big integer. This form uses the capitals
     * 'O' and 'I' to distinguish them from the numeric digits that are used in composite representations of
     * prefix-free codes.
     * @param n The number to represent
     * @return The string of binary digits
     */
    public static String toBinary(BigInteger n) {
        String result = n.toString(2);
        result = result.replace('0', 'O');
        result = result.replace('1', 'I');
        return result;
    }

    /**
     * Returns the length of the bit-sequence that is represented by the prefix-free encoding of the given number.
     * @param n The number to evaluate
     * @return The description length of the number
     */
    public static long descriptionLength(BigInteger n) {
        if (n.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("Value should be non-negative");
        }
        return internalDescriptionLength(n.add(BigInteger.ONE));
    }

    private static long internalDescriptionLength(BigInteger n) {
        long result;
        int sign = n.compareTo(BigInteger.ONE);
        if (sign < 0) {
            throw new IllegalArgumentException("Value should be at least unity");
        } else if (sign == 0) {
            result = 1;
        } else {
            long bitLength = n.bitLength();
            result = internalDescriptionLength(BigInteger.valueOf(bitLength - 1)) + bitLength;
        }
        return result;
    }

}
