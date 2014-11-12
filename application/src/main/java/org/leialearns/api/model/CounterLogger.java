package org.leialearns.api.model;

import org.leialearns.api.structure.Node;
import org.leialearns.bridge.BridgeOverride;

public interface CounterLogger {

    @BridgeOverride
    void logCounters(Counted counted);

    public void logCounters(Version... version);

    @BridgeOverride
    void logCounters(Node node, Version... version);
}
