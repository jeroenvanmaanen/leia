package org.leialearns.logic.model.histogram;

import org.leialearns.api.model.histogram.Counter;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.logic.model.Version;
import org.leialearns.logic.structure.Node;

import static org.leialearns.utilities.Display.displayParts;

public class TransientCounter implements Counter {
    private final Symbol symbol;
    private long value = 0L;

    public TransientCounter(Symbol symbol) {
        this.symbol = symbol;
    }

    public void setId(@SuppressWarnings("unused") Long id) {
        throw new UnsupportedOperationException();
    }

    public Long getId() {
        return null;
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public Node getNode() {
        return null;
    }

    @Override
    public Version getVersion() {
        return null;
    }

    @Override
    public void increment() {
        increment(1L);
    }

    @Override
    public void increment(long amount) {
        if (value + amount < 0) {
            throw new IllegalStateException("Underflow: " + this);
        }
        value += amount;
    }

    @Override
    public TransientCounter fresh() {
        return this;
    }

    @Override
    public String toString() {
        return displayParts("TransientCounter", symbol, value);
    }

}
