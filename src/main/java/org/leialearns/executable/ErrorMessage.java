package org.leialearns.executable;

/**
 * Provides a clean way to end the application with a message to the user.
 */
public class ErrorMessage extends RuntimeException {
    private String message;

    /**
     * Creates a new <code>ErrorMessage</code> instance.
     * @param message The message to the user
     */
    public ErrorMessage(String message) {
        this(message, null);
    }

    /**
     * Creates a new <code>ErrorMessage</code> instance.
     * @param cause The throwable that caused this message
     */
    public ErrorMessage(Throwable cause) {
        this(null, cause);
    }

    /**
     * Creates a new <code>ErrorMessage</code> instance.
     * @param message The message to the user
     * @param cause The throwable that caused this message
     */
    public ErrorMessage(String message, Throwable cause) {
        StringBuilder builder = new StringBuilder();
        if (message != null) {
            builder.append(message);
        }
        if (cause != null) {
            if (builder.length() > 0) {
                builder.append(": ");
            }
            builder.append(cause.getMessage());
        }
        this.message = builder.toString();
    }

    /**
     * Returns the message to the user.
     * @return The message to the user
     */
    public String getMessage() {
        return message;
    }

}
