package org.leialearns.graph.model;

import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.session.RootDTO;
import org.leialearns.logic.model.Fraction;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.utilities.Display.asDisplay;
import static org.leialearns.utilities.Static.getLoggingClass;

public class FractionDAO {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private FractionOracleRepository fractionOracleRepository;

    @Autowired
    private FractionEstimateRepository fractionEstimateRepository;

    public TypedIterable<FractionBaseDTO> findFractions(RootDTO root) {
        return new TypedIterable<FractionBaseDTO>(FractionBaseDTO.class, fractionOracleRepository.findAll());
    }

    public FractionBaseDTO findFraction(RootDTO root, long index) {
        FractionBaseDTO result;
        result = fractionOracleRepository.findByIndex(index);
        if (result != null && result.getId() == null) {
            result = null;
        }
        logger.trace("Find fraction: {} -> [{}]", index, asDisplay(result));
        return result;
    }

    public FractionBaseDTO findOrCreateFraction(RootDTO root, Fraction fraction) {
        long index = fraction.getIndex();
        long numerator = fraction.getNumerator();
        long denominator = fraction.getDenominator();
        return findOrCreateFraction(root, index, numerator, denominator);
    }

    public FractionBaseDTO findOrCreateFraction(RootDTO root, long index, long numerator, long denominator) {
        return createFraction(root, index, numerator, denominator);
    }

    public FractionBaseDTO createFraction(RootDTO root, long index, long numerator, long denominator) {
        FractionEstimateDTO fraction = new FractionEstimateDTO();
        fraction.setNumerator(numerator);
        fraction.setDenominator(denominator);
        FractionBaseDTO result = fractionEstimateRepository.save(fraction);
        return result;
    }

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
        return result;
    }

}
