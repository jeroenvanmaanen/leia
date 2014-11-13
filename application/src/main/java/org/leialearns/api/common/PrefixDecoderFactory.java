package org.leialearns.api.common;

import java.io.InputStream;
import java.io.Reader;

public interface PrefixDecoderFactory {
    PrefixDecoder createReadablePrefixEncoder(Reader reader);
    PrefixDecoder createBinaryPrefixEncoder(InputStream input);
}
