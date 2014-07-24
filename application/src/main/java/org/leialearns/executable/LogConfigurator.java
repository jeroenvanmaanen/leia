package org.leialearns.executable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.LogManager;

/**
 * Configures the logging system.
 */
public class LogConfigurator {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final String logDir;

    /**
     * Creates a new <code>LogConfigurator</code> instance.
     * @param logDir The directory for log files
     * @throws IOException
     */
    public LogConfigurator(final String logDir)
    throws IOException {
        this.logDir = logDir;
        File logDirFile = new File(logDir);
        if (!logDirFile.exists() && !logDirFile.mkdirs()) {
            log.warn("Failed to create directory: " + logDir);
        }
    }

    /**
     * Configures the logging system using the given logging properties. The logging properties resource is
     * filtered to replace '<code>LOG_DIR</code>' by the path of the directory where the log files must be placed.
     * @param loggingProperties
     * @throws IOException
     */
    public void configure(InputStream loggingProperties)
    throws IOException {
        System.err.println("Configuring logging using log directory: " + logDir);
        Reader logConfigReader = new InputStreamReader(loggingProperties);
        InputStream logConfigStream = new LineFilterInputStream(logConfigReader, new LineFilter() {
            @Override
            public String filterLine(String line) {
                return line.replaceAll("LOG_DIR", logDir);
            }
        });
        LogManager logManager = LogManager.getLogManager();
        logManager.readConfiguration(logConfigStream);
    }

}
