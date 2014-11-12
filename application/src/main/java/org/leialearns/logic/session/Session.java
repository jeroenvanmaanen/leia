package org.leialearns.logic.session;

import org.leialearns.enumerations.AccessMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.api.interaction.InteractionContext;
import org.leialearns.logic.model.Counted;
import org.leialearns.api.model.common.NodeDataProxy;
import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.logic.model.Toggled;
import org.leialearns.logic.model.Version;
import org.leialearns.api.structure.Node;

/**
 * Defines an object that can be used to create and/or obtain {@link org.leialearns.logic.model.Version}s of
 * various types of models. A session is assumed to be associated with a single thread of execution.
 */
public interface Session {

    /**
     * Returns the root object associated with this session.
     * @return The root object associated with this session
     */
    Root getRoot();

    /**
     * Returns the interaction context associated with this session.
     * @return The interaction context associated with this session
     */
    InteractionContext getInteractionContext();

    /**
     * Creates a new version instance. The ordinal of the new version is guaranteed to be greater than all versions
     * created or obtained earlier in the same session (assuming that the session is associated with a single thread
     * of execution).
     * @param modelType The model type for the new version
     * @return The new version instance
     */
    Version createVersion(ModelType modelType);

    /**
     * Returns the version that is associated with the given ordinal. Returns <code>null</code> if no such
     * version exists.
     * @param ordinal The ordinal of the version
     * @return The version that is associated with the given ordinal
     */
    Version findVersion(long ordinal);

    /**
     * Returns the last version of the given model type and with the given access mode. Returns <code>null</code>
     * if no such version exists.
     * @param modelType The model type of the requested version
     * @param accessMode The access model of the requested version
     * @return The last version that matches the parameters
     */
    Version findLastVersion(ModelType modelType, AccessMode accessMode);

    /**
     * Returns the last version of the given model type and with the given access mode. Returns a new version
     * object if no such version exists.
     * @param modelType The model type of the requested version
     * @param accessMode The access model of the requested version
     * @return The last version that matches the parameters
     */
    Version findOrCreateLastVersion(ModelType modelType, AccessMode accessMode);

    /**
     * Logs the given versions. This produces a log record for each combination of a node (either an internal node
     * or a leaf) in the structure that underlies the versions and a symbol that was recorded for that node.
     * Each log record logs the path in the structure tree, the symbol and the values for that combination
     * of node and symbol in the respective versions.
     * @param label A descriptive label for the block of log records
     * @param versions The versions to log
     */
    void logVersions(String label, Version.Iterable versions);

    /**
     * Creates a new <code>Counted</code> object for this session.
     * @return A new <code>Counted</code> object
     */
    Counted createCountedVersion();

    /**
     * Creates a new <code>Toggled</code> object for this session.
     * @param node The node that is toggled
     * @param include Tha flag that indicates whether the given node is included in the new model of expected behavior
     * @return A new <code>Toggled</code> object for this session
     */
    Toggled createToggledVersion(Node node, boolean include);

    /**
     * Retrieves a histogram from the cache.
     * @param version The version of the requested histogram
     * @param node The node of the requested histogram
     * @return The requested histogram
     */
    Histogram getHistogram(Version version, Node node);

    /**
     * Stores the given histogram in the cache
     * @param histogram The histogram to store
     */
    void putHistogram(NodeDataProxy<Histogram> histogram);

    /**
     * Returns a fresh copy of the session that is (re)attached to the ORM framework.
     * @return A fresh copy of the session
     */
    Session refresh();

    /**
     * Flushes all cached data to persistent storage.
     */
    void flush();

}
