package org.leialearns.command.observer;

import org.leialearns.api.enumerations.AccessMode;
import org.leialearns.api.enumerations.ModelType;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.api.model.Counted;
import org.leialearns.api.model.Observed;
import org.leialearns.api.model.Version;
import org.leialearns.api.session.Root;
import org.leialearns.api.session.Session;
import org.leialearns.common.ExceptionWrapper;
import org.leialearns.common.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.leialearns.common.Static.getLoggingClass;

/**
 * Creates a new version of the model of observed behavior.
 */
public class Observer implements org.leialearns.api.command.Observer {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private Observed lastCreated = null;
    private final Setting<String> interactionContextUri = new Setting<>("Interaction context URI");

    @Autowired
    private Root root;

    /**
     * Sets the URL of the interaction context to use.
     * @param interactionContextUri The URL of the interaction context to use
     */
    public void setInteractionContextUri(String interactionContextUri) {
        this.interactionContextUri.set(interactionContextUri);
    }

    /**
     * Returns a new session object for this observer.
     * @return A new session object for this observer
     */
    protected Session createSession() {
        return root.createSession(interactionContextUri.get());
    }

    /**
     * Returns the last <code>Observed</code> object created by this observer.
     * @return The last <code>Observed</code> object created by this observer
     */
    public Observed getLastCreated() {
        return lastCreated;
    }

    /**
     * Creates a new version of the model of observed behavior.
     * See <a href="http://htmlpreview.github.io/?https://github.com/jeroenvanmaanen/leia/blob/master/doc/create-observed.html" target="_blank">create observed</a>
     * for a description of the steps.
     *
     * @param args Command line arguments
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void command(String... args) {
        lastCreated = null;
        Session session = createSession();

        Version newObservedVersion = session.createVersion(ModelType.OBSERVED);
        try {
            Version oldObservedVersion = newObservedVersion.findLastBefore(ModelType.OBSERVED, null);
            logger.debug("Last observed version: [" + oldObservedVersion + "]");
            if (oldObservedVersion != null && oldObservedVersion.getAccessMode() != AccessMode.READABLE) {
                logger.info("Another process is updating the model of observed behavior");
                newObservedVersion.setAccessMode(AccessMode.EXCLUDE, session);
            } else {
                logger.debug("New observed version: [" + newObservedVersion + "]");
                Observed newObserved = newObservedVersion.createObservedVersion();
                logger.debug("New observed: [" + newObserved + "]");
                lastCreated = newObserved;
                Observed oldObserved;
                if (oldObservedVersion == null) {
                    oldObserved = null;
                } else {
                    oldObserved = oldObservedVersion.createObservedVersion();
                    newObserved.copyCountersFromLastObserved(oldObservedVersion);
                }

                newObserved.attachCounted(oldObserved);
                Counted counted = newObserved.getCounted();
                logger.debug("Counted: [" + counted + "]");

                if (counted == null) {
                    logger.info("No READABLE COUNTED versions added after the last COUNTED version included in the last OBSERVED version");
                    newObservedVersion.setAccessMode(AccessMode.EXCLUDE, session);
                } else {
                    Version newDelta = newObserved.getOrCreateDelta();
                    logger.debug("Delta: [" + newDelta + "]");

                    newObserved.copyCountersFromLastDelta(oldObserved);
                    newObserved.adjustDeltaForToggledNodes(oldObserved);

                    newObserved.createCountersFromRecentCounted(oldObserved);

                    newObserved.updateCounters(oldObserved);

                    try {
                        newObservedVersion.waitForLock(session);
                    } catch (InterruptedException exception) {
                        throw ExceptionWrapper.wrap(exception);
                    }
                    logger.debug("Set DELTA version to READABLE: [" + newObserved.getDeltaVersion() + "]");
                    newObserved.getDeltaVersion().setAccessMode(AccessMode.READABLE, session);
                    logger.debug("Set OBSERVED version to READABLE: [" + newObservedVersion + "]");
                    newObservedVersion.setAccessMode(AccessMode.READABLE, session);
                    long minOrdinal = oldObservedVersion == null ? 0 : oldObservedVersion.getOrdinal();
                    InteractionContext context = session.getInteractionContext();
                    Version.Iterable versions = context.findVersionsInRange(minOrdinal, newObservedVersion.getOrdinal(), ModelType.COUNTED, null);
                    session.logVersions("" + minOrdinal + ": " + newObservedVersion.getOrdinal(), versions);
                }
                // newObservedVersion.setAccessMode(AccessMode.EXCLUDE, session);
            }
        } finally {
            if (newObservedVersion.getAccessMode() != AccessMode.READABLE) {
                newObservedVersion.setAccessMode(AccessMode.EXCLUDE, session);
            }
        }
        logger.debug("Access mode: {}: {}", newObservedVersion, newObservedVersion.getAccessMode());
    }

}
