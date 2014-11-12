package org.leialearns.logic.model.histogram;

import org.leialearns.api.model.histogram.DeltaDiff;
import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.api.model.histogram.HistogramFactory;
import org.leialearns.api.model.histogram.Modifiable;
import org.leialearns.enumerations.HistogramOperator;
import org.leialearns.logic.structure.Node;

public class DeltaDiffImpl implements DeltaDiff {
    private final Node node;
    private final Histogram deltaAdditions;
    private final Histogram deltaSubtractions;
    private final HistogramFactory histogramFactory;

    public DeltaDiffImpl(Node node, String label, HistogramFactory histogramFactory) {
        this.node = node;
        this.histogramFactory = histogramFactory;
        String suffix = (label != null && label.length() > 0 ? ": " + label : "");
        deltaAdditions = createTransientHistogram("Delta additions" + suffix);
        deltaAdditions.setLocation(() -> String.valueOf(node));
        deltaSubtractions = createTransientHistogram("Delta subtractions" + suffix);
        deltaSubtractions.setLocation(() -> String.valueOf(node));
    }

    public Node getNode() {
        return node;
    }

    public Histogram getDeltaAdditions() {
        return deltaAdditions;
    }

    public Histogram getDeltaSubtractions() {
        return deltaSubtractions;
    }

    @Override
    public void add(Histogram histogram) {
        deltaAdditions.add(histogram);
    }

    @Override
    public void subtract(Histogram histogram) {
        deltaSubtractions.add(histogram);
    }

    public void addTo(Modifiable modifiable) {
        modifiable.add(deltaAdditions);
        modifiable.subtract(deltaSubtractions);
    }

    public void subtractFrom(Modifiable modifiable) {
        modifiable.add(deltaSubtractions);
        modifiable.subtract(deltaAdditions);
    }

    public void modify(HistogramOperator operator, Modifiable modifiable) {
        switch (operator) {
            case ADD_TO:
                addTo(modifiable);
                break;
            case SUBTRACT_FROM:
                subtractFrom(modifiable);
                break;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private Histogram createTransientHistogram(String label) {
        Histogram result = histogramFactory.createHistogram();
        result.setLabel(label);
        return result;
    }

    @Override
    public String toString() {
        return "[DeltaDiff|" + node + "|+" + deltaAdditions.getWeight() + "/-" + deltaSubtractions.getWeight() + "]";
    }
}
