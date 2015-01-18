package org.leialearns.api.common;

/**
 * Interface for objects that can be encoded in a prefix-free manner.
 */
public interface PrefixFree {

    /**
     * Encodes the contents of this object using the given prefix-free encoder.
     * @param encoder The prefix-free encoder to use
     */
    void prefixEncode(PrefixEncoder encoder);

    /**
     * Restores the contents of this object using the data from the given prefix-free decoder.
     * @param decoder The prefix-free decoder to use
     */
    void prefixDecode(PrefixDecoder decoder);
}
