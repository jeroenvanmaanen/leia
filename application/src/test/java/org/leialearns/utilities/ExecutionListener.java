package org.leialearns.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.getLoggingClass;

public class ExecutionListener extends AbstractTestExecutionListener {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Override
    public void beforeTestMethod(TestContext testContext) {
        logger.info("Start test: " + display(testContext.getTestMethod()));
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        Throwable throwable = testContext.getTestException();
        if (throwable != null) {
            logger.error("Exception in test", throwable);
        }
    }

}
