package org.leialearns.command.encounter;

import java.io.InputStream;

/**
 * Defines a token source that is backed by an {@link java.io.InputStream}.
 */
public interface StreamAdapter extends TokenSource {

    /**
     * Sets the input stream that backs this stream adapter.
     * @param stream The input stream that backs this stream adapter
     */
    void setInputStream(InputStream stream);

    void setSkip(long skip);

    void setLimit(Long limit);

}
