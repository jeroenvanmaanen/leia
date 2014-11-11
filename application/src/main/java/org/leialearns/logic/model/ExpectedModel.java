package org.leialearns.logic.model;

import org.leialearns.enumerations.ModelType;
import org.leialearns.logic.model.expectation.Expectation;
import org.leialearns.logic.structure.Node;

public interface ExpectedModel extends TypedVersionExtension {
    static final ExpectedModel EMPTY = new ExpectedModel() {
        @Override
        public void setObserved(Observed observed) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Observed getObserved() {
            return null;
        }

        @Override
        public boolean isIncluded(Node node) {
            return false;
        }

        @Override
        public Expectation getExpectation(Node node) {
            return null;
        }

        @Override
        public Version getVersion() {
            return null;
        }

        @Override
        public ModelType getModelType() {
            return null;
        }

        @Override
        public String toString() {
            return "[ExpectedModel.EMPTY]";
        }
    };
    boolean isIncluded(Node node);
    Expectation getExpectation(Node node);
    Observed getObserved();
    void setObserved(Observed observed);
}
