package org.leialearns.api.command;

/**
 * Provides the uniform {@link Command#command(String[])} method for all sub-commands of the LEIA application.
 */
public interface Command {

    /**
     * Executes the command with the given arguments.
     * @param args The arguments to use
     */
    void command(String... args);

}
