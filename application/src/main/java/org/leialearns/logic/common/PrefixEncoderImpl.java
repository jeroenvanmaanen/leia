package org.leialearns.logic.common;

import org.leialearns.api.common.PrefixEncoder;
import org.leialearns.logic.utilities.PrefixEncoderWrapper;

import java.io.Writer;

public class PrefixEncoderImpl extends PrefixEncoderWrapper implements PrefixEncoder {

    public PrefixEncoderImpl(Writer writer) {
        super(writer);
    }
}
