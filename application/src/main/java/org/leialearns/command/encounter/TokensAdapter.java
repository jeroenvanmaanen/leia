package org.leialearns.command.encounter;

import org.leialearns.utilities.ExceptionWrapper;
import org.leialearns.utilities.NullStream;
import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.leialearns.utilities.Static.getLoggingClass;

/**
 * Parses the input from the backing input stream to a stream of tokens. The concatenation of the tokens
 * produced by a tokens adapter is the same as the stream of characters that the original input stream encodes.
 */
public class TokensAdapter implements StreamAdapter {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Setting<Pattern> tokenPattern = new Setting<>("Token pattern", () -> Pattern.compile("(.|\n)"));
    private final LinkedList<String> tokens = new LinkedList<>();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(new NullStream()));
    private long skip = 0L;
    private Long limit = null;

    /**
     * Sets the regular expression that is used to match each token. For any given non-empty string, it
     * should match a prefix of at least one character.
     * @param tokenPattern The regular expression that is used to match each token
     */
    public void setTokenPattern(String tokenPattern) {
        this.tokenPattern.set(Pattern.compile(tokenPattern));
    }

    @Override
    public void setInputStream(InputStream stream) {
        try {
            reader.close();
        } catch (IOException exception) {
            logger.warn("Exception while closing reader: " + exception.getMessage());
            logger.debug("Stack trace", exception);
        }
        reader = new BufferedReader(new InputStreamReader(stream));
        tokens.clear();
    }

    public void setSkip(long skip) {
        this.skip = skip;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    /**
     * <em>See {@link java.util.Iterator#hasNext()}.</em>
     */
    @Override
    public boolean hasNext() {
        if (tokens.isEmpty()) {
            getMoreTokens();
        }
        return !tokens.isEmpty();
    }

    /**
     * <em>See {@link java.util.Iterator#next}.</em>
     */
    @Override
    public String next() {
        if (tokens.isEmpty()) {
            getMoreTokens();
        }
        return tokens.removeFirst();
    }

    /**
     * Throws UnsupportedOperationException.
     * @throws UnsupportedOperationException <strong>Always</strong>
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected void getMoreTokens() {
        String line;
        try {
            while (skip > 0) {
                reader.readLine();
                skip--;
            }
            if (limit == null || limit > 0) {
                line = reader.readLine();
                if (limit != null) {
                    limit--;
                }
                if (line != null) {
                    line = "\n" + line + "\r";
                }
            } else {
                line = null;
            }
        } catch (IOException e) {
            throw ExceptionWrapper.wrap(e);
        }
        if (line != null) {
            int pos = 0;
            Matcher matcher = tokenPattern.get().matcher(line);
            while (matcher.find(pos)) {
                if (matcher.start() > pos) {
                    tokens.add(line.substring(pos, matcher.start()));
                }
                String group = matcher.group();
                if (group.equals("")) {
                    tokens.add("" + line.charAt(pos++));
                } else {
                    tokens.add(group);
                    pos = matcher.end();
                }
            }
            if (pos < line.length()) {
                tokens.add(line.substring(pos));
            }
        }
    }
}
