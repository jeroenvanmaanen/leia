package org.leialearns.logic.prefixfree;

import org.leialearns.api.common.PrefixDecoder;
import org.leialearns.logic.utilities.PrefixDecoderWrapper;

import java.io.Reader;

public class PrefixDecoderImpl extends PrefixDecoderWrapper implements PrefixDecoder {

    public PrefixDecoderImpl(Reader reader) {
        super(reader);
    }

    @Override
    public <E extends Enum<E>> E nextEnum(Class<E> type) {
        return super.nextEnumConstant(type); // Why is it necessary to override this method?!
    }
}
