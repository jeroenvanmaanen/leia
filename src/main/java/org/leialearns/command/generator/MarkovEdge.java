package org.leialearns.command.generator;

import java.math.BigInteger;

public class MarkovEdge {
    private final MarkovNode to;
    private final BigInteger weight;
    private final String symbol;

    public MarkovEdge(BigInteger weight, String symbol, MarkovNode to) {
        this.to = to;
        this.weight = weight;
        this.symbol = symbol;
    }

    public MarkovNode getTo() {
        return to;
    }

    public BigInteger getWeight() {
        return weight;
    }

    public String getSymbol() {
        return symbol;
    }
}
