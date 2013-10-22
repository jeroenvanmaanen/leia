package org.leialearns.command.consolidator;

import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.model.Expected;
import org.leialearns.logic.model.Observed;
import org.leialearns.logic.model.Toggled;
import org.leialearns.logic.model.Version;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.session.Session;
import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.leialearns.utilities.Static.getLoggingClass;

/**
 * Consolidates toggled versions into a version of the model of expected behavior that can be queried efficiently.
 */
public class Consolidator implements org.leialearns.command.api.Consolidator {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final Setting<String> interactionContextUri = new Setting<String>("Interaction context URI");
    private final Expected lastExpected = null;

    @Autowired
    private Root root;

    /**
     * Sets the URL of the interaction context to use.
     * @param interactionContextUri The URL of the interaction context to use
     */
    public void setInteractionContextUri(String interactionContextUri) {
        this.interactionContextUri.set(interactionContextUri);
    }

    public Expected getLastExpected() {
        return lastExpected;
    }

    /**
     * Returns a new session object for this consolidator.
     * @return A new session object for this consolidator
     */
    protected Session createSession() {
        return root.createSession(interactionContextUri.get());
    }

    /**
     * Consolidates the model of expected behavior.
     * @param args The arguments to use
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void command(String... args) {
        Session session = createSession();
        Toggled previousToggled;
        Version previousExpectedVersion = session.findLastVersion(ModelType.EXPECTED, AccessMode.READABLE);
        logger.debug("Previous expected version: " + previousExpectedVersion);
        if (previousExpectedVersion != null) {
            Expected previousExpected = previousExpectedVersion.createExpectedVersion();
            previousToggled = previousExpected.getToggled();
        } else {
            previousToggled = null;
        }
        logger.debug("Previous toggled: " + previousToggled);
        long previousToggledOrdinal = (previousToggled == null ? 0L : previousToggled.getVersion().getOrdinal());
        Version lastToggledVersion = session.findLastVersion(ModelType.TOGGLED, AccessMode.READABLE);
        logger.debug("Last toggled: " + lastToggledVersion);
        if (lastToggledVersion != null && previousToggledOrdinal < lastToggledVersion.getOrdinal()) {
            Version maxToggledVersion = lastToggledVersion.findRangeMax(previousExpectedVersion, ModelType.TOGGLED, AccessMode.READABLE);
            logger.debug("Max toggled version: " + maxToggledVersion);
            if (maxToggledVersion != null) {
                long maxToggledOrdinal = maxToggledVersion.getOrdinal();
                logger.debug("Max toggled ordinal: " + maxToggledOrdinal);
                Version.Iterable toggledVersions = session.findVersionsInRange(previousToggledOrdinal, maxToggledOrdinal, ModelType.TOGGLED, AccessMode.READABLE);
                if (!toggledVersions.isEmpty()) {
                    Version newExpectedVersion = session.createVersion(ModelType.EXPECTED);
                    logger.debug("New expected version: " + newExpectedVersion);
                    Expected newExpected = newExpectedVersion.createExpectedVersion();

                    if (previousExpectedVersion != null) {
                        newExpected.copyEstimates(previousExpectedVersion);
                    }

                    Observed newObserved = null;
                    Version newToggledVersion = null;
                    for (Version toggledVersion : toggledVersions) {
                        logger.debug("Toggled: " + toggledVersion);
                        newExpected.copyEstimates(toggledVersion);
                        if (newToggledVersion == null || toggledVersion.getOrdinal() > newToggledVersion.getOrdinal()) {
                            newToggledVersion = toggledVersion;
                        }
                        Toggled toggled = toggledVersion.findToggledVersion();
                        Observed toggledObserved = toggled.getObserved();
                        if (toggledObserved!= null && (newObserved == null || toggledObserved.getVersion().getOrdinal() > newObserved.getVersion().getOrdinal())) {
                            newObserved = toggledObserved;
                        }
                    }

                    if (newToggledVersion != null) {
                        Toggled newToggled = newToggledVersion.findToggledVersion();
                        logger.debug("New toggled: " + newToggled);
                        newExpected.setToggled(newToggled);
                    }

                    if (newObserved != null) {
                        newExpected.setObserved(newObserved);
                    }

                    newExpectedVersion.setAccessMode(AccessMode.READABLE, session);
                    logger.debug("New expected: " + newExpected);
                    newExpected.logCounters();
                }
            }
        }
    }

}
