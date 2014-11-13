package org.leialearns.api.common;

import java.io.OutputStream;
import java.io.Writer;

public interface PrefixEncoderFactory {
    PrefixEncoder createReadablePrefixEncoder(Writer writer);
    PrefixEncoder createBinaryPrefixEncoder(OutputStream output);
}
