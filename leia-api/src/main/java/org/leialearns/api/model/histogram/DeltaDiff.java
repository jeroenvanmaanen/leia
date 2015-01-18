package org.leialearns.api.model.histogram;

import org.leialearns.api.enumerations.HistogramOperator;

public interface DeltaDiff extends Modifiable {
    Histogram getDeltaAdditions();
    Histogram getDeltaSubtractions();
    void addTo(Modifiable modifiable);
    void subtractFrom(Modifiable modifiable);
    void modify(HistogramOperator operator, Modifiable modifiable);
}
