package org.leialearns.api.model.expectation;

import org.leialearns.api.common.NodeData;
import org.leialearns.api.interaction.Symbol;
import org.leialearns.common.TypedIterable;

import java.util.Collection;
import java.util.Map;

public interface Expectation extends NodeData<Estimate> {
    void setFractions(Map<Symbol,Fraction> fractions);
    TypedIterable<Symbol> getSymbols();
    Fraction getFraction(Symbol symbol);
    String prefixEncode(Collection<Symbol> symbols);
    long descriptionLength(Iterable<Symbol> symbols);
}
