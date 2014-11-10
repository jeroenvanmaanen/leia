package org.leialearns.logic.model;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.model.common.HasTypeLabel;
import org.leialearns.logic.model.common.NodeData;
import org.leialearns.utilities.TypedIterable;

import java.util.Collection;
import java.util.Map;

public interface Expectation extends NodeData<Expectation,Estimate.Iterable>, HasTypeLabel {
    void setFractions(Map<Symbol,Fraction> fractions);
    TypedIterable<Symbol> getSymbols();
    Fraction getFraction(Symbol symbol);
    String prefixEncode(Collection<Symbol> symbols);
    long descriptionLength(Collection<Symbol> symbols);
}
