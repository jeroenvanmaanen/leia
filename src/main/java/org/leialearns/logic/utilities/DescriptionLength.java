package org.leialearns.logic.utilities;

import org.leialearns.utilities.ExceptionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;

/**
 * Provides static functions to calculate the description length of a {@link java.math.BigInteger}. See
 * {@link HasDescriptionLength} for more information on the importance of description lengths in the context of
 * <b>L</b>Ex<i>Au</i>.
 */
public class DescriptionLength {
    private static final Logger LOGGER = LoggerFactory.getLogger(DescriptionLength.class);

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
     * Returns an ascii form of the prefix-free encoding of the given number. No prefix-free encoding is a prefix of
     * the prefix-free encoding of another number. The actual bit sequence consisting of the capitals 'O' and 'I' is
     * enriched with digits and symbols to enhance the readability of the code.
     * @param n The number to represent
     * @return The prefix-free code for the given number
     */
    public static String prefixEncode(BigInteger n) {
        LOGGER.debug("Start prefix encode big integer: [" + n + "]");
        if (n.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("Value should be non-negative");
        }
        return prefixEncode(n.add(BigInteger.ONE), 'I');
    }

    private static String prefixEncode(BigInteger n, char lastChunkFlag) {
        LOGGER.trace("Prefix encode big integer: [" + n + "]");
        String result;
        if (n.compareTo(BigInteger.ONE) == 0) {
            result = lastChunkFlag + ":1(1)";
        } else {
            String remainder = toBinary(n).substring(1);
            StringBuilder builder = new StringBuilder();
            builder.append(prefixEncode(BigInteger.valueOf(remainder.length()), 'O'));
            builder.append('/');
            builder.append(lastChunkFlag);
            builder.append(":1");
            builder.append(remainder);
            builder.append('(');
            builder.append(n.toString());
            builder.append(')');
            result = builder.toString();
        }
        return result;
    }

    public static BigInteger prefixDecode(Reader reader) {
        BigInteger value = BigInteger.ZERO;
        boolean isLength;
        long length;
        do {
            isLength = readBit(reader) == 0;
            length = value.longValue();
            value = BigInteger.ONE;
            for (long i = 0; i < length; i++) {
                value = value.shiftLeft(1);
                if (readBit(reader) != 0) {
                    value = value.add(BigInteger.ONE);
                }
            }
        } while (isLength);
        return value.subtract(BigInteger.ONE);
    }

    private static int readBit(Reader reader) {
        int result = -1;
        try {
            while (result < 0) {
                int ch = reader.read();
                switch (ch) {
                    case 'O':
                        result = 0;
                        break;
                    case 'I':
                        result = 1;
                        break;
                    case -1:
                        throw new IOException("End of stream reached");
                }
            }
        } catch (IOException exception) {
            throw ExceptionWrapper.wrap(exception);
        }
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
