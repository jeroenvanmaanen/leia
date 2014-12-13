package org.leialearns.command.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static org.leialearns.common.Static.getLoggingClass;

public class MarkovNode {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private final MarkovEdge toSelf;
    private final Collection<MarkovEdge> edges = new ArrayList<>();
    private BigInteger norm = null;

    public MarkovNode() {
        toSelf = new MarkovEdge(BigInteger.ZERO, "", this);
    }

    public void add(MarkovEdge edge) {
        edges.add(edge);
        norm = null;
    }

    public MarkovEdge generate(Random random) {
        if (norm == null) {
            BigInteger newNorm = BigInteger.ZERO;
            for (MarkovEdge edge : edges) {
                newNorm = newNorm.add(edge.getWeight());
            }
            norm = newNorm;
        }
        MarkovEdge result = toSelf;
        if (norm.signum() > 0) {
            BigInteger choice = draw(random, norm);
            for (MarkovEdge edge : edges) {
                BigInteger weight = edge.getWeight();
                logger.debug("Compare: {} < {}", choice, weight);
                if (choice.compareTo(weight) < 0) {
                    logger.debug("Select");
                    result = edge;
                    break;
                }
                choice = choice.subtract(weight);
            }
        }
        logger.debug("Generate edge: {}", result);
        return result;
    }

    protected BigInteger draw(Random random, BigInteger limit) {
        BigInteger result;
        do {
            result = new BigInteger(limit.bitLength(), random);
        } while (result.compareTo(limit) >= 0);
        logger.debug("Draw: {} -> {}", limit, result);
        return result;
    }
}
