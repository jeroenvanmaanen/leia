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

/**
 * Helper for the {@link Expectation} near type. The interface {@link ExpectationHelper} is necessary, because the
 * proxy created as a result of the @{@link Transactional} annotation hides the superclass (but exposes the interface).
 */
public class ExpectationHelperImpl implements ExpectationHelper {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private Root root;

    @Override
    public void setRoot(Root root) {
        this.root = root;
    }

    @Override
    public Expectation createExpectation(Root root) {
        return new ExpectationObject(root);
    }

    @Override
    public Expectation getExpectation(ExpectedModel expectedModel, Node node) {
        return getExpectation(expectedModel.getVersion(), node);
    }

    @Override
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
    @Override
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
