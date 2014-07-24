package org.leialearns.executable;

/**
 * Provides a method that can be called line by line to filter a text resource.
 */
public interface LineFilter {

    /**
     * Performs substitutions on a single line of a text resource.
     * @param line The line to filter
     * @return The filtered line
     */
    String filterLine(String line);

}
