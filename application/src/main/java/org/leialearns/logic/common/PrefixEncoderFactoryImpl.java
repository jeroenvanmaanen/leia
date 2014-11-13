package org.leialearns.logic.common;

import org.leialearns.api.common.PrefixEncoder;
import org.leialearns.api.common.PrefixEncoderFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

@Component
public class PrefixEncoderFactoryImpl implements PrefixEncoderFactory {

    @Override
    public PrefixEncoder createReadablePrefixEncoder(Writer writer) {
        return new PrefixEncoderImpl(writer);
    }

    @Override
    public PrefixEncoder createBinaryPrefixEncoder(OutputStream output) {
        return new PrefixEncoderImpl(createBinaryWriter(output));
    }

    protected BinaryWriter createBinaryWriter(OutputStream output) {
        return new BinaryWriter(output);
    }

    protected class BinaryWriter extends Writer {
        byte buffer = 0;
        int bits = 0;
        private OutputStream output;
        protected BinaryWriter(OutputStream output) {
            this.output = output;
        }
        @Override
        public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
            int length = Math.min(off + len, cbuf.length);
            for (int i = off; i < length; i++) {
                char ch = cbuf[i];
                if (ch == 'O') {
                    writeBit(0);
                } else if (ch == 'I') {
                    writeBit(1);
                }
            }
        }

        protected void writeBit(int bit) throws IOException {
            if (bits > 0) {
                buffer <<= 1;
            }
            buffer |= bit;
            bits++;
            if (bits > 7) {
                output.write(buffer);
                buffer = 0;
                bits = 0;
            }
        }

        @Override
        public void flush() throws IOException {
            while (bits > 0) {
                writeBit(1); // Encodes the number zero
            }
            output.flush();
        }

        @Override
        public void close() throws IOException {
            flush();
            output.close();
        }
    }
}
