package org.leialearns.logic.common;

import org.leialearns.api.common.PrefixDecoder;
import org.leialearns.api.common.PrefixDecoderFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

@Component
public class PrefixDecoderFactoryImpl implements PrefixDecoderFactory {

    @Override
    public PrefixDecoder createReadablePrefixEncoder(Reader reader) {
        return new PrefixDecoderImpl(reader);
    }

    @Override
    public PrefixDecoder createBinaryPrefixEncoder(InputStream input) {
        return new PrefixDecoderImpl(createBinaryReader(input));
    }

    protected BinaryReader createBinaryReader(InputStream input) {
        return new BinaryReader(input);
    }

    protected class BinaryReader extends Reader {
        private InputStream input;
        char[] bits;
        int index = 8;
        protected BinaryReader(InputStream input) {
            this.input = input;
        }

        @Override
        public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
            int i = 0;
            int p = off;
            int ch;
            while (i < len && p < cbuf.length && (ch = readBit()) >= 0) {
                cbuf[p] = (char) ch;
                i++;
                p++;
            }
            return i;
        }

        protected int readBit() throws IOException {
            if (index < 1) {
                throw new IllegalStateException("Attempt to read past the end of the stream");
            }
            if (index >= 8) {
                int buffer = input.read();
                if (buffer < 0) {
                    index = -1;
                    return -1;
                } else {
                    bits = Integer.toBinaryString((buffer & 0xFF) | 0x100).substring(1).replace('0', 'O').replace('1', 'I').toCharArray();
                    index = 0;
                }
            }
            return bits[index++];
        }

        @Override
        public void close() throws IOException {
            input.close();
        }
    }
}
