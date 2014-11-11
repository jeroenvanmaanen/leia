package org.leialearns.graph.model;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.graph.session.RootDTO;
import org.leialearns.logic.model.expectation.Fraction;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.utilities.Display.asDisplay;
import static org.leialearns.utilities.Static.gcd;
import static org.leialearns.utilities.Static.getLoggingClass;

public class FractionDAO {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private FractionOracleRepository fractionOracleRepository;

    @Autowired
    private FractionEstimateRepository fractionEstimateRepository;

    @BridgeOverride
    public TypedIterable<FractionBaseDTO> findFractions(RootDTO root) {
        return new TypedIterable<>(FractionBaseDTO.class, fractionOracleRepository.findAll());
    }

    @BridgeOverride
    public FractionBaseDTO findFraction(RootDTO root, long index) {
        FractionBaseDTO result;
        result = fractionOracleRepository.findByIndex(index);
        if (result != null && result.getId() == null) {
            result = null;
        }
        logger.trace("Find fraction: {} -> [{}]", index, asDisplay(result));
        return result;
    }

    @BridgeOverride
    public FractionBaseDTO findOrCreateFraction(RootDTO root, Fraction fraction) {
        long index = fraction.getIndex();
        long numerator = fraction.getNumerator();
        long denominator = fraction.getDenominator();
        return findOrCreateFraction(root, index, numerator, denominator);
    }

    public FractionBaseDTO findOrCreateFraction(RootDTO root, long index, long numerator, long denominator) {
        return createFraction(root, index, numerator, denominator);
    }

    @BridgeOverride
    public FractionBaseDTO createFraction(RootDTO root, long index, long numerator, long denominator) {
        FractionEstimateDTO fraction = new FractionEstimateDTO();
        fraction.setNumerator(numerator);
        fraction.setDenominator(denominator);
        return fractionEstimateRepository.save(fraction);
    }

    @BridgeOverride
    public FractionBaseDTO createFraction(RootDTO root, long index, long numerator, long denominator, boolean inOracle) {
        FractionBaseDTO result;
        if (inOracle) {
            FractionOracleDTO fraction = new FractionOracleDTO();
            fraction.setIndex(index);
            fraction.setNumerator(numerator);
            fraction.setDenominator(denominator);
            result = fractionOracleRepository.save(fraction);
        } else {
            result = createFraction(root, index, numerator, denominator);
        }
        logger.trace("Created fraction: {}", result);
        return result;
    }

    public FractionBaseDTO add(FractionBaseDTO fraction, FractionBaseDTO other) {
        long a = fraction.getNumerator();
        long b = fraction.getDenominator();
        long c = other.getNumerator();
        long d = other.getDenominator();
        long gcd = gcd(b, d);
        d = d / gcd;
        long denominator = b * d;
        b = b / gcd;
        long numerator = (a * d) + (c * b);
        return createFraction(null, -1, numerator, denominator);
    }

}
