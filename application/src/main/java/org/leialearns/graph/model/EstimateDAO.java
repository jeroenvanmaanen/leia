package org.leialearns.graph.model;

import org.leialearns.api.enumerations.ModelType;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.graph.interaction.SymbolDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.utilities.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.leialearns.utilities.Static.getLoggingClass;

public class EstimateDAO extends IdDaoSupport<EstimateDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private EstimateRepository repository;

    @Autowired
    FractionEstimateRepository fractionEstimateRepository;

    @Autowired
    ToggledRepository toggledRepository;

    @Autowired
    FractionDAO fractionDAO;

    @Override
    protected EstimateRepository getRepository() {
        return repository;
    }

    public EstimateDTO findEstimate(VersionDTO version, NodeDTO node, SymbolDTO symbol) {
        return repository.findEstimate(version, node, symbol);
    }

    @BridgeOverride
    public TypedIterable<EstimateDTO> findEstimates(VersionDTO version, NodeDTO node) {
        Set<EstimateDTO> result = repository.findEstimatesByVersionAndNode(version, node);
        return new TypedIterable<>(result, EstimateDTO.class);
    }

    public TypedIterable<EstimateDTO> findEstimates(VersionDTO version) {
        Set<EstimateDTO> estimates = repository.findEstimates(version);
        return new TypedIterable<>(estimates, EstimateDTO.class);
    }

    @BridgeOverride
    public EstimateDTO createEstimate(ExpectedDTO expected, NodeDTO node, SymbolDTO symbol, FractionBaseDTO fraction) {
        return findOrCreate(expected.getVersion(), node, symbol, fraction);
    }

    @BridgeOverride
    public EstimateDTO createEstimate(ToggledDTO toggled, NodeDTO node, SymbolDTO symbol, FractionBaseDTO fraction) {
        return findOrCreate(toggled.getVersion(), node, symbol, fraction);
    }

    public EstimateDTO findOrCreate(VersionDTO version, NodeDTO node, SymbolDTO symbol, FractionBaseDTO fraction) {
        EstimateDTO result = findEstimate(version, node, symbol);
        if (result == null) {
            result = create(version, node, symbol, fraction);
        } else {
            if (logger.isTraceEnabled()) {
                String fractionString = String.valueOf(fraction);
                logger.trace("Existing persistent fraction for: " + version + ": " + node + ": " + symbol + " -> " + fractionString);
            }
            if (!fraction.equals(result.getFraction())) {
                throw new IllegalStateException("Existing estimate does not match fraction: " + result + ": " + fraction);
            }
        }
        return result;
    }

    public EstimateDTO create(VersionDTO version, NodeDTO node, SymbolDTO symbol, FractionBaseDTO fraction) {
        FractionEstimateDTO fractionEstimate;
        if (fraction instanceof FractionEstimateDTO) {
            fractionEstimate = (FractionEstimateDTO) fraction;
        } else {
            fractionEstimate = (FractionEstimateDTO) fractionDAO.findOrCreateFraction(null, -1, fraction.getNumerator(), fraction.getDenominator());
        }
        logger.trace("Create estimate for: " + version + ": " + node + ": " + symbol + ": " + fraction);
        EstimateDTO estimate = new EstimateDTO();
        estimate.setVersion(version);
        estimate.setNode(node);
        estimate.setSymbol(symbol);
        estimate.setFraction(fractionEstimate);
        estimate = save(estimate);
        logger.trace("Created estimate: " + estimate);
        return estimate;
    }

    @BridgeOverride
    public void copyEstimates(final ExpectedDTO expected, final VersionDTO sourceVersion) {
        VersionDTO targetVersion = expected.getVersion();
        logger.debug("Copy estimates: {} => {}", sourceVersion, targetVersion);
        Set<NodeDTO> nodes = repository.findEstimateNodes(sourceVersion);
        logger.trace("Nodes: {");
        for (NodeDTO node : nodes) {
            logger.trace("  Node: {}", node);
            Integer affected = repository.deleteEstimates(targetVersion, node);
            logger.debug("Deleted: {} estimate nodes", affected);
        }
        logger.trace("}");

        if (sourceVersion.getModelType() == ModelType.TOGGLED) {
            ToggledDTO toggled = toggledRepository.findByVersion(sourceVersion);
            logger.debug("Toggled: {}", toggled);
            if (!toggled.getInclude()) {
                Integer affected = repository.deleteEstimates(targetVersion, toggled.getNode());
                logger.debug("Deleted: {} estimate nodes", affected);
            }
        }
        Integer affected = fractionEstimateRepository.deleteUnusedFractions();
        logger.debug("Deleted {} unused fractions", affected);

        int count = 0;
        try {
            for (EstimateDTO estimate : findEstimates(sourceVersion)) {
                create(targetVersion, estimate.getNode(), estimate.getSymbol(), estimate.getFraction());
                count++;
            }
        } finally {
            logger.debug("Copied estimates: " + count);
        }
    }

}
