package org.leialearns.logic.model;

import com.google.common.base.Function;
import org.leialearns.enumerations.AccessMode;
// import org.leialearns.enumerations.AgentMode;
import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.interaction.InteractionContext;
import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.session.Session;
import org.leialearns.logic.structure.Node;
import org.leialearns.bridge.NearIterable;

// import java.util.Collection;

public interface Version extends Comparable<Version> {
    Long getOrdinal();
    ModelType getModelType();
    AccessMode getAccessMode();
    Version findLastBefore(ModelType modelType, AccessMode accessMode);
    Version findRangeMax(Version previousVersion, ModelType modelType, AccessMode accessMode);
    void setAccessMode(AccessMode accessMode, Session session);
    void waitForLock(Session session) throws InterruptedException;
    InteractionContext getInteractionContext();
    Session getOwner();

    /*
    Collection<Session> getWriters();
    Collection<Session> getReaders();
    void registerWriter(Session writer, AgentMode agentMode);
    void registerReader(Session reader, AgentMode agentMode);
    */

    Counted createCountedVersion();
    Observed createObservedVersion();
    Expected createExpectedVersion();
    Toggled findToggledVersion();
    Counter.Iterable findCounters();
    Counter.Iterable findCounters(Node node);
    Counter.Iterable findCounters(Function<Node,Node.Iterable> getChildren, Function<Node,Boolean> getInclude);
    Counter findCounter(Node node, Symbol symbol);
    Counter findOrCreateCounter(Node node, Symbol symbol);
    Estimate.Iterable findEstimates(Node node);

    interface Iterable extends NearIterable<Version> {
        Version declareNearType();
    }
}
