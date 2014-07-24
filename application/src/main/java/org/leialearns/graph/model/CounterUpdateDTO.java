package org.leialearns.graph.model;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.bridge.FarObject;
import org.leialearns.logic.model.CounterUpdate;

public class CounterUpdateDTO implements FarObject<CounterUpdate> {
    private final CounterDTO counter;
    private long amount = 0;

    public CounterUpdateDTO(CounterDTO counter) {
        if (counter == null) {
            throw new IllegalArgumentException("Counter should not be null");
        }
        this.counter = counter;
    }

    public CounterDTO getCounter() {
        return counter;
    }

    @BridgeOverride
    public long getAmount() {
        return amount;
    }

    public void increment(long amount) {
        this.amount += amount;
    }

    public CounterUpdate declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
