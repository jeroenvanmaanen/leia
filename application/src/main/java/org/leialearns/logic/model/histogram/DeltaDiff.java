package org.leialearns.logic.model.histogram;

import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.api.model.histogram.Modifiable;

public interface DeltaDiff extends Modifiable {
    Histogram getDeltaAdditions();
    Histogram getDeltaSubtractions();
    void addTo(Modifiable modifiable);
    void subtractFrom(Modifiable modifiable);
    void modify(HistogramOperator operator, Modifiable modifiable);
}
