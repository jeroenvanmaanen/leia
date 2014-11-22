package org.leialearns.logic.common;

import org.leialearns.api.common.PrefixDecoder;
import org.leialearns.logic.utilities.Bit;
import org.leialearns.logic.utilities.PrefixFree;
import org.leialearns.utilities.ExceptionWrapper;
import scala.math.BigInt;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class PrefixDecoderImpl implements PrefixDecoder {
    private final Reader reader;
    private final Map<Class<?>,Object> helpers = new HashMap<>();

    public PrefixDecoderImpl(Reader reader) {
        this.reader = reader;
    }

    @Override
    public int nextInt() {
        return nextBigInteger().intValue();
    }

    @Override
    public long nextLong() {
        return nextBigInteger().longValue();
    }

    @Override
    public BigInteger nextBigInteger() {
        BigInt bigInt = PrefixFree.prefixDecode(reader);
        return bigInt.bigInteger();
    }

    @Override
    public int nextInt(int length) {
        return nextBigInteger(length).intValue();
    }

    @Override
    public long nextLong(int length) {
        return nextBigInteger(length).longValue();
    }

    @Override
    public BigInteger nextBigInteger(int length) {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < length; i++) {
            Bit bit = PrefixFree.readBit(reader);
            result = result.shiftLeft(1);
            if (bit.asInt() > 0) {
                result = result.setBit(0);
            }
        }
        return result;
    }

    @Override
    public String nextString() {
        int length = nextInt();
        byte[] buffer = new byte[length];
        for (int i = 0; i < length; i++) {
            buffer[i] = (byte) nextInt(8);
        }
        try {
            return new String(buffer, 0, length, "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            throw ExceptionWrapper.wrap(exception);
        }
    }

    @Override
    public boolean nextBoolean() {
        int code = nextInt(1);
        return code >= 1;
    }

    @Override
    public <E extends Enum<E>> E nextEnum(Class<E> type) {
        E[] constants = type.getEnumConstants();
        int last = constants.length - 1;
        if (last >= 0) {
            int bitLength = BigInteger.valueOf(last).bitLength();
            int index = nextInt(bitLength);
            return constants[index];
        }
        return null;
    }

    @Override
    public <T> void addHelper(T helper, Class<T> type) {
        helpers.put(type, helper);
    }

    @Override
    public <T> T getHelper(Class<T> type) {
        return type.cast(helpers.get(type));
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
