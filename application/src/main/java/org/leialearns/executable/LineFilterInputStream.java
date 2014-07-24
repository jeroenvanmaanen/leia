package org.leialearns.executable;

import org.leialearns.utilities.Setting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * <p>Provides an input stream that filters a text resource.</p>
 *
 * <p>This class is used to filter the logging configuration, so there is no point in logging through a
 * {@link org.slf4j.Logger}. If the verbose setting is <code>true</code>, then the result of the filtered
 * resource is written to standard error.</p>
 */
public class LineFilterInputStream extends InputStream {
    private BufferedReader reader;
    private LineFilter lineFilter;
    private byte[] buffer = new byte[] { 10 };
    private int index = 0;
    private final Setting<Boolean> verbose = new Setting<Boolean>("Verbose", false);

    /**
     * Creates a new <code>LineFilterInputStream</code> instance.
     * @param reader The reader that backs this stream
     * @param lineFilter The line filter to use
     * @throws IOException
     */
    public LineFilterInputStream(Reader reader, LineFilter lineFilter) throws IOException {
        this(reader, lineFilter, null);
    }

    /**
     * Creates a new <code>LineFilterInputStream</code> instance.
     * @param reader The reader that backs this stream
     * @param lineFilter The line filter to use
     * @param verbose The verbose flag to use
     * @throws IOException
     */
    public LineFilterInputStream(Reader reader, LineFilter lineFilter, Boolean verbose) throws IOException {
        if (verbose != null) {
            this.verbose.set(verbose);
        }
        if (reader == null) {
            throw new IllegalArgumentException("The reader should not be null");
        }
        if (lineFilter == null) {
            throw new IllegalArgumentException("The lineFilter should not be null");
        }
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader) reader;
        } else {
            this.reader = new BufferedReader(reader);
        }
        this.lineFilter = lineFilter;
        nextLine();
    }

    @Override
    public int read() throws IOException {
        if (index >= buffer.length) {
            nextLine();
        }
        int result;
        if (index < buffer.length) {
            result = buffer[index++];
        } else {
            result = -1;
        }
        return result;
    }

    protected void nextLine() throws IOException {
        if (buffer.length > 0) {
            String line;
            line = this.reader.readLine();
            if (line == null) {
                buffer = new byte[0];
            } else {
                line = this.lineFilter.filterLine(line);
                line += '\n';
                buffer = line.getBytes("utf-8");
            }
            index = 0;
            if (verbose.get()) {
                System.err.print("Line: ");
                System.err.print(line);
            }
        }
    }

}
