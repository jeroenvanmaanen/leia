package org.leialearns.utilities;

import java.io.FilterInputStream;

/**
 * Provides a stream that does not produce any input.
 */
public class NullStream extends FilterInputStream {

    /**
     * Creates a new <code>NullStream</code> instance.
     */
    public NullStream() {
        super(System.in);
    }

    /**
     * Always returns <code>-1</code> (no more input).
     * @return <code>-1</code>
     */
    public int read() {
        return -1;
    }

}
