package org.leialearns.logic.common;

import org.leialearns.api.common.PrefixEncoder;
import org.leialearns.logic.utilities.PrefixFree;
import org.leialearns.utilities.ExceptionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigInteger;

import static java.lang.String.format;

public class PrefixEncoderImpl implements PrefixEncoder {
    private static final Logger logger = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());
    private final Writer writer;

    public PrefixEncoderImpl(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void append(int i) {
        append(BigInteger.valueOf(i));
    }

    @Override
    public void append(long i) {
        append(BigInteger.valueOf(i));
    }

    @Override
    public void append(BigInteger i) {
        try {
            writer.write(PrefixFree.prefixEncode(i));
        } catch (IOException exception) {
            throw ExceptionWrapper.wrap(exception);
        }
        appendComment("\n");
    }

    @Override
    public void append(int i, int length) {
        append(BigInteger.valueOf(i), length);
    }

    @Override
    public void append(long i, int length) {
        append(BigInteger.valueOf(i), length);
    }

    @Override
    public void append(BigInteger i, int length) {
        if (i.signum() < 0) {
            throw new IllegalArgumentException("Cannot prefix-encode a negative number: " + i);
        }
        String denotation = i.toString(2).replace('0', 'O').replace('1', 'I');
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("Denotation: [%s]", denotation));
        }

        int padding = length - denotation.length();
        if (padding < 0) {
            throw new IllegalArgumentException(format("Number too large: %s: %s", length, i.toString(16).toUpperCase()));
        }
        try {
            for (; padding > 0; padding--) {
                writer.write('O');
            }
            writer.write(denotation);
        } catch (IOException exception) {
            throw ExceptionWrapper.wrap(exception);
        }
        appendOriginal(String.valueOf(i));
        appendComment("\n");
    }

    @Override
    public void append(String s) {
        byte[] bytes;
        try {
            bytes = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException exception) {
            throw ExceptionWrapper.wrap(exception);
        }
        append(bytes.length);
        for (byte b : bytes) {
            append(b & 0xFF, 8);
        }
        appendOriginal(format("\"%s\"", s.replace('"', '\'')));
        appendComment("\n");
    }

    @Override
    public void append(boolean b) {
        append(b ? 1 : 0, 1);
        appendOriginal(Boolean.toString(b));
        appendComment("\n");
    }

    @Override
    public <E extends Enum<E>> void append(E e) {
        int last = e.getClass().getEnumConstants().length - 1;
        if (last > 0) {
            int bitLength = BigInteger.valueOf(last).bitLength();
            append(e.ordinal(), bitLength);
            appendOriginal(e.name());
            appendComment("\n");
        }
    }

    @Override
    public void appendComment(String s) {
        try {
            writer.write(s.replace('O', '0').replace('I', '1'));
        } catch (IOException exception) {
            throw ExceptionWrapper.wrap(exception);
        }
    }

    @Override
    public void appendOriginal(String s) {
        appendComment("(");
        appendComment(s);
        appendComment(")");
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
