package org.leialearns.api.common;

import java.io.Closeable;
import java.math.BigInteger;

public interface PrefixDecoder extends Closeable {
    int nextInt();
    long nextLong();
    BigInteger nextBigInteger();
    int nextInt(int length);
    long nextLong(int length);
    BigInteger nextBigInteger(int length);
    String nextString();
}
