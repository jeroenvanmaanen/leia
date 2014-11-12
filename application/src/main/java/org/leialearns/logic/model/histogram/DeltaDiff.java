package org.leialearns.logic.model.histogram;

import org.leialearns.api.model.histogram.Histogram;
import org.leialearns.api.model.histogram.Modifiable;
import org.leialearns.logic.model.Observed;
import org.leialearns.logic.structure.Node;

public class DeltaDiff implements Modifiable {
    private final Node node;
    private final Histogram deltaAdditions;
    private final Histogram deltaSubtractions;

    public DeltaDiff(Node node, Observed observed, String label) {
        this.node = node;
        String suffix = (label != null && label.length() > 0 ? ": " + label : "");
        deltaAdditions = observed.createTransientHistogram("Delta additions" + suffix);
        deltaAdditions.setLocation(() -> String.valueOf(node));
        deltaSubtractions = observed.createTransientHistogram("Delta subtractions" + suffix);
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

    public void modify(Operator operator, Modifiable modifiable) {
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

    public static Operator getOperatorAdd(boolean addFlag) {
        return addFlag ? Operator.ADD_TO : Operator.SUBTRACT_FROM;
    }

    public static Operator getOperatorSubtract(boolean subtractFlag) {
        return subtractFlag ? Operator.SUBTRACT_FROM : Operator.ADD_TO;
    }

    @Override
    public String toString() {
        return "[DeltaDiff|" + node + "|+" + deltaAdditions.getWeight() + "/-" + deltaSubtractions.getWeight() + "]";
    }

    public interface Map extends java.util.Map<Node,DeltaDiff> {}
    public enum Operator {
        ADD_TO,
        SUBTRACT_FROM
    }
}
