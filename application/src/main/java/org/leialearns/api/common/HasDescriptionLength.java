package org.leialearns.api.common;

/**
 * <p>Provides a method to get the description length of the implementing object. This depends of the availability
 * of a prefix-free encoding for objects in a suitable context (<em>e.g.</em>, an encoding of symbols in the
 * context of an alphabet, or an encoding of numbers in the context of the set of integers) that assigns a bit
 * sequence to this particular object that is as long as the number returned by this method.</p>
 *
 * <p>The description length of objects is crucial in <b>LEIA</b>, because the whole point of
 * <b>LEIA</b> is to prove that the age old saw that "a computer only produces what you put into it" can
 * be transcended by a clever two-way pincer application of the
 * <a href="http://en.wikipedia.org/wiki/Minimum_description_length" target="_blank">Minimum Description
 * Length Principle</a>.</p>
 *
 * <p>See the
 * <a href="http://en.wikipedia.org/wiki/Prefix_code" target="_blank">Wikipedia page on prefix-free codes</a> and
 * <a href="https://www.google.nl/search?q=an+introduction+to+kolmogorov+complexity+and+its+applications" target="_blank">An
 * Introduction to Kolmogorov Complexity and its Applications</a> for a discussion of prefix-free codes.</p>
 */
public interface HasDescriptionLength {

    /**
     * Returns the description length of this object. See
     * {@link HasDescriptionLength} for more information on the importance of description lengths in the context of
     * <b>LEIA</b>.
     * @return The description length of this object
     */
    long descriptionLength();

}
