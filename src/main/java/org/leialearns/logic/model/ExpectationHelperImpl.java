package org.leialearns.logic.model;

import org.leialearns.logic.interaction.Symbol;
import org.leialearns.logic.session.Root;
import org.leialearns.logic.structure.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.leialearns.utilities.Static.getLoggingClass;
import static org.leialearns.utilities.Static.toList;

public class ExpectationHelperImpl implements ExpectationHelper {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private Root root;

    public void setRoot(Root root) {
        this.root = root;
    }

    public Expectation createExpectation(Root root) {
        return new ExpectationObject(root);
    }

    public Expectation getExpectation(ExpectedModel expectedModel, Node node) {
        return getExpectation(expectedModel.getVersion(), node);
    }

    public Expectation getExpectation(Expected expected, Node node) {
        return getExpectation(expected.getVersion(), node);
    }

    public Expectation getExpectation(Toggled toggled, Node node) {
        return getExpectation(toggled.getVersion(), node);
    }

    protected Expectation getExpectation(Version version, Node node) {
        // Root root = version.getOwner().getRoot();
        Expectation result = createExpectation(root);
        result.set(version, node);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void attach(Toggled toggled, Root root, Node node, Expectation expectation) {
        if (node == null) {
            throw new IllegalArgumentException("The node should not be null");
        }
        if (expectation == null) {
            logger.debug("No expectation: " + toggled + ": " + node);
        } else {
            List<Symbol> symbols = toList(expectation.getSymbols());
            logger.debug("Attach: " + toggled + ": " + node + ": " + expectation.descriptionLength(symbols));
            for (Symbol symbol : symbols) {
                Fraction fraction = expectation.getFraction(symbol);
                logger.trace("Fraction: " + symbol + ": " + fraction);
                if (fraction.getNumerator() > 0) {
                    if (fraction instanceof TransientFraction) {
                        fraction = root.findOrCreateFraction(fraction);
                        logger.trace("Fraction: " + symbol + ": " + fraction);
                    }
                    Estimate estimate = toggled.createEstimate(node, symbol, fraction);
                    logger.debug("Estimate: " + estimate);
                }
            }
            toggled.getVersion().getOwner().flush();
        }
    }

}
