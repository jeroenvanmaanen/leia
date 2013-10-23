package org.leialearns.executable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.leialearns.command.Command;
import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Provides the <code>main</code> method for the LExAu executable.
 */
public class App {
    private Logger logger = null;

    /**
     * Creates a new <code>App</code> instance.
     */
    public App() {
    }

    /**
     * Instantiates this class and calls the execute method on it.
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        int rc = -1;

        App application = null;
        String message = null;
        Throwable cause = null;
        try {
            application = new App();
            application.execute(args);
            rc = 0;
        } catch (ErrorMessage errorMessage) {
            message = errorMessage.getMessage();
            System.err.println(errorMessage.getMessage());
        } catch (Throwable exception) {
            message = exception.getMessage();
            if (message == null) {
                message = exception.toString();
            }
            cause = exception;
            System.err.println(exception.getMessage());
            exception.printStackTrace();
        }
        Logger logger = application == null ? null : application.getLogger();
        if (message != null) {
            System.err.println(message);
            if (logger != null) {
                logger.info(message);
            }
            if (cause != null) {
                if (logger == null) {
                    cause.printStackTrace();
                } else {
                    logger.debug("Stack trace", cause);
                }
            }
        } else if (logger != null) {
            logger.info("Done");
        }
        System.exit(rc);
    }

    /**
     * Initializes logging, finds the command bean that corresponds to the first command line argument
     * and passes the remaining command line arguments to its {@link org.leialearns.command.Command#command(String[])} method.
     * @param args The command line arguments
     * @throws Exception In case of an exception
     */
    public void execute(String[] args) throws Exception {
        final Setting<Boolean> verbose = new Setting<Boolean>("Verbose", Boolean.FALSE);
        Options options = new Options();
        options.addOption("v", "verbose", false, "Produce additional output for debugging purposes");
        options.addOption("l", "log-directory", true, "The directory used to store the log files");
        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args, true);
        } catch (ParseException exception) {
            throw new ErrorMessage("Command line parsing failed: " + exception.getMessage());
        }
        if (cmd != null) {
            if (cmd.hasOption('v')) {
                verbose.set(true);
            }
            String logDir;
            if (cmd.hasOption('l')) {
                logDir = cmd.getOptionValue('l');
                if (verbose.get()) {
                    System.err.print("Log directory: ");
                    System.err.println(logDir);
                }
                File logDirFile = new File(logDir);
                if (!logDirFile.isDirectory()) {
                    if (!logDirFile.mkdirs()) {
                        throw new ErrorMessage("Could not create directory for log files: " + logDir);
                    }
                }
            } else {
                logDir = "/tmp";
            }

            InputStream loggingProperties = App.class.getResourceAsStream("/logging.properties");
            if (verbose.get()) {
                System.err.print("Logging properties stream: ");
                System.err.println(loggingProperties);
            }
            new LogConfigurator(logDir).configure(loggingProperties);

            logger = LoggerFactory.getLogger(App.class);
            logger.info(".\n.\n.\n.");
            logger.info("Start LExAu");

            logger.debug("Verbose: " + (cmd.hasOption('v') ? "true" : "false"));

            String[] subArgs = cmd.getArgs();
            logger.debug("Number of sub arguments: " + subArgs.length);
            if (subArgs.length < 1) {
                System.err.println("No sub-command specified");
                System.exit(1);
            }
            String sub_command = subArgs[0];
            if (subArgs.length > 1) {
                subArgs = Arrays.copyOfRange(subArgs, 1, subArgs.length);
            } else {
                subArgs = new String[0];
            }
            logger.debug("Run [" + sub_command + "]: " + subArgs.length);
            GenericApplicationContext environment = new GenericApplicationContext();
            DefaultListableBeanFactory beanFactory = environment.getDefaultListableBeanFactory();
            environment.refresh();
            String[] configLocations = new String[] {"ApplicationContext.xml", "MainApplicationContext.xml"};
            ApplicationContext context = new ClassPathXmlApplicationContext(configLocations, environment);
            Command obj = (Command) context.getBean(sub_command);
            obj.command(subArgs);
        }
    }

    protected Logger getLogger() {
        return logger;
    }

}
