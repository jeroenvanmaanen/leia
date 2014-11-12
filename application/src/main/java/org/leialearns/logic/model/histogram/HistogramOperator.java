package org.leialearns.logic.model.histogram;

import java.util.HashMap;
import java.util.Map;

public enum HistogramOperator {
    ADD_TO,
    SUBTRACT_FROM,
    ;
    private static Map<HistogramOperator,HistogramOperator> invert = new HashMap<>();
    static {
        invert.put(ADD_TO, SUBTRACT_FROM);
        invert.put(SUBTRACT_FROM, ADD_TO);
    }
    public HistogramOperator derive(boolean identity) {
        return identity ? this : invert.get(this);
    }
}
