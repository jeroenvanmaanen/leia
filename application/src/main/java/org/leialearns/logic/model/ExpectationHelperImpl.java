package org.leialearns.logic.model;

import org.leialearns.api.interaction.Symbol;
import org.leialearns.api.model.ExpectedModel;
import org.leialearns.api.model.Toggled;
import org.leialearns.api.model.Version;
import org.leialearns.api.model.common.NodeDataProxy;
import org.leialearns.api.model.expectation.Estimate;
import org.leialearns.api.model.expectation.Expectation;
import org.leialearns.api.model.expectation.Fraction;
import org.leialearns.api.session.Root;
import org.leialearns.api.structure.Node;
import org.leialearns.logic.model.common.NodeDataProxyImpl;
import org.leialearns.logic.model.expectation.ExpectationObject;
import org.leialearns.logic.model.expectation.TransientFraction;
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
        NodeDataProxy<Expectation> proxy = getExpectationProxy(version, node);
        return proxy.getData();
    }

    protected NodeDataProxy<Expectation> getExpectationProxy(Version version, Node node) {
        Expectation result = createExpectation(root);
        NodeDataProxy<Expectation> proxy = new NodeDataProxyImpl<>();
        proxy.setData(result);
        result.retrieve(() -> version.findEstimates(node));
        proxy.set(version, node);
        return proxy;
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
