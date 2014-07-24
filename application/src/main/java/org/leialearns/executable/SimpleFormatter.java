package org.leialearns.executable;

import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.leialearns.utilities.Static.join;

/**
 * <p>Provides a formatter that can be used with <code>java.util.logging</code> to produce single-line log-entries that
 * are reasonably compact and are prefixed with a sort-proof time-stamp.</p>
 *
 * <p>If the verbosity level of the special logger named <code>stack-trace</code> is at least DEBUG, then stack traces
 * are reformatted to make it easier to dismiss the technicalities of AOP related stack frames. If the
 * verbosity level of the special logger named <code>stack-trace</code> is at least TRACE, these stack-frames are
 * indented, otherwise every block of one or more of these stack frames is replaced by "<code>[...]</code>".</p>
 */
public class SimpleFormatter extends Formatter {
    private static final Logger logger = LoggerFactory.getLogger("stack-trace");
    private static final Pattern ORGANISATION_RE = Pattern.compile("^([^.]*[.][^.]*)[.](.*)$");
    private static final ThreadLocal<DateFormat> DATE_FORMATTER = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        }
    };
    private static final String[] DISMISS_RE_PARTS = new String[] {
            "com[.]sun[.]proxy[.]",
            "java[.]lang[.]reflect|sun[.]reflect[.]",
            "net[.]sf[.]cglib",
            "org[.]apache[.]maven[.]",
            "org[.]hibernate[.]",
            "org[.]junit[.]",
            "org[.]leialearns[.]bridge[.]BridgeFactory[.$]",
            "org[.]neo4j[.]",
            "org[.]springframework[.]",
            "sun[.]proxy[.]",
            "[^(]*CGLIB[$][$]",
    };
    private static final Pattern DISMISS_RE = Pattern.compile("^([\t ]*at )(" + join("|", DISMISS_RE_PARTS) + ")(.*\n)", Pattern.MULTILINE);
    private static final Pattern CAUSED_BY_RE = Pattern.compile("^Caused by", Pattern.MULTILINE);
    private static final Pattern HEAD_RE = Pattern.compile("^((?:(?:[\t ]*at   |[^\t ])[^\n]*\n)*).*", Pattern.DOTALL);
    private static final Pattern DELETE_RE = Pattern.compile("(^[\t ]*at   .*\n)+", Pattern.MULTILINE);
    private final Setting<Boolean> regexFormat = new SilentSetting<Boolean>("Regex format flag", false);

    /**
     * Creates a new <code>SimpleFormatter</code> instance.
     */
    public SimpleFormatter() {
    }

    /**
     * Sets property <code>regexFormat</code>. When this property is set to <code>true</code>, the
     * <em>organization</em> and <em>sourceClass</em> fields in the log record are filled by a very simple
     * regular expression match on the <code>sourceClassName</code> property of the <code>logRecord</code>.
     *
     * @param flag The value to use for <code>regexFormat</code>
     */
    @SuppressWarnings("unused")
    public void setRegexFormat(boolean flag) {
        regexFormat.set(flag);
    }

    /**
     * Formats a <code>logRecord</code>.
     *
     * @param logRecord The <code>logRecord</code> to format
     * @return The string that contains the formatted record
     */
    @Override
    public String format(LogRecord logRecord) {
        StringBuilder builder = new StringBuilder();
        long millis = logRecord.getMillis();
        String timestamp = DATE_FORMATTER.get().format(new Date(millis));
        String level = logRecord.getLevel().getName();
        String message = logRecord.getMessage();
        message = message.replace("\n", "\n        ");
        String organisation = "";
        String sourceClass = logRecord.getSourceClassName();
        if (regexFormat.get()) {
            Matcher matcher = ORGANISATION_RE.matcher(sourceClass);
            if (matcher.matches()) {
                organisation = matcher.group(1);
                sourceClass = matcher.group(2);
            }
        } else {
            StringTokenizer tokenizer = new StringTokenizer(sourceClass, ".");
            List<String> organisationList = new ArrayList<String>();
            LinkedList<String> sourceClassList = new LinkedList<String>();
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (organisationList.size() < 2) {
                    organisationList.add(token);
                } else {
                    sourceClassList.addFirst(token);
                }
            }
            if (!sourceClassList.isEmpty()) {
                organisation = implode(".", organisationList);
                ListIterator<String> it = sourceClassList.listIterator();
                boolean first = true;
                int length = 0;
                do {
                    String part = it.next();
                    int partLength = part.length();
                    boolean wasFirst = first;
                    if (first) {
                        first = false;
                    } else {
                        partLength++;
                    }
                    if (wasFirst || length + partLength < 40) {
                        length += partLength;
                    } else {
                        it.previous();
                        break;
                    }
                } while (it.hasNext());
                List<String> partList = new ArrayList<String>();
                while (it.hasPrevious()) {
                    String part = it.previous();
                    partList.add(part);
                }
                sourceClass = implode(".", partList);
            }
        }
        String sourceMethod = logRecord.getSourceMethodName();
        boolean first = true;
        for (String part : new String[] { timestamp, fix(level, 8), organisation, sourceClass, sourceMethod, message }) {
            if (first) {
                first = false;
            } else {
                builder.append("|");
            }
            builder.append(part);
        }
        builder.append('\n');
        Throwable thrown = logRecord.getThrown();
        if (thrown != null && logger.isDebugEnabled()) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            thrown.printStackTrace(printWriter);
            printWriter.close();
            String stackTrace = stringWriter.toString();
            stackTrace = DISMISS_RE.matcher(stackTrace).replaceAll("$1  $2$3");
            if (!logger.isTraceEnabled()) {
                try {
                    StringBuilder stackBuilder = new StringBuilder();
                    Matcher causedBy = CAUSED_BY_RE.matcher(stackTrace);
                    int pos = 0;
                    String block;
                    while (causedBy.find()) {
                        int nextPos = causedBy.start();
                        block = stackTrace.substring(pos, nextPos);
                        pos = nextPos;
                        stackBuilder.append(reduceStackBlock(block));
                    }
                    block = stackTrace.substring(pos);
                    stackBuilder.append(reduceStackBlock(block));
                    stackTrace = stackBuilder.toString();
                } catch (Throwable throwable) {
                    stackTrace = ">>> REDUCE FAILED: " + throwable + "\n" + stackTrace;
                }
            }
            builder.append(stackTrace);
        }
        return builder.toString();
    }

    protected String reduceStackBlock(String block) {
        String result;
        Matcher matcher = HEAD_RE.matcher(block);
        if (matcher == null || !matcher.matches()) {
            result = ">>> SPLIT FAILED\n" + block;
        } else {
            String head = matcher.group(1);
            String tail = block.substring(matcher.end(1));
            tail = DELETE_RE.matcher(tail).replaceAll("\t[...]\n");
            result = head + tail;
        }
        return result;
    }

    protected String fix(String field, int width) {
        StringBuilder result = new StringBuilder();
        int n;
        n = field.length();
        if (n > width) {
            result.append(field.substring(n - width));
        } else {
            result.append(field);
        }
        n = result.length();

        while (n < width) {
            result.insert(0, ' ');
            n++;
        }
        return result.toString();
    }

    protected String implode(String separator, List<String> parts) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String part : parts) {
            if (first) {
                first = false;
            } else {
                builder.append(separator);
            }
            builder.append(part);
        }
        return builder.toString();
    }

    protected class SilentSetting<T> extends Setting<T> {
        protected SilentSetting(String label, T defaultValue) {
            super(label, defaultValue);
        }

        @Override
        public Logger getLogger() {
            return NOPLogger.NOP_LOGGER;
        }
    }
}
