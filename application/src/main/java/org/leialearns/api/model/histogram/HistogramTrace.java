package org.leialearns.api.model.histogram;

import org.leialearns.api.interaction.Symbol;

import java.util.Collection;

public interface HistogramTrace {
    String getName();
    Throwable getOrigin();
    boolean isTransient();
    HistogramTrace findNonTransient();
    void collectSymbols(Collection<Symbol> symbols);
    void logParts(String indent);
    void addValues(StringBuilder builder, Symbol symbol);
}
