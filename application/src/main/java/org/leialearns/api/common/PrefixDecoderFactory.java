package org.leialearns.api.common;

import java.io.InputStream;
import java.io.Reader;

public interface PrefixDecoderFactory {
    PrefixDecoder createReadablePrefixDecoder(Reader reader);
    PrefixDecoder createBinaryPrefixDecoder(InputStream input);
}
