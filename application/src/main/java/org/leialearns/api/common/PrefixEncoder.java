package org.leialearns.api.common;

import java.io.Closeable;
import java.math.BigInteger;

public interface PrefixEncoder extends Closeable {
    void append(int i);
    void append(long i);
    void append(BigInteger i);
    void append(int i, int length);
    void append(long i, int length);
    void append(BigInteger i, int length);
    void append(String s);
}
