package org.leialearns.graph.model;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.graph.common.IdDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.common.Static.getLoggingClass;

public class ExpectedDAO extends IdDaoSupport<ExpectedDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private ExpectedRepository repository;

    @Override
    protected ExpectedRepository getRepository() {
        return repository;
    }

    public ExpectedDTO find(VersionDTO version) {
        return repository.findExpectedByVersion(version);
    }

    public ExpectedDTO create(VersionDTO version) {
        ExpectedDTO dto = new ExpectedDTO();
        dto.setVersion(version);
        dto.setId(version.getId());
        logger.debug("Version of DTO: [" + dto.getVersion() + "]");
        ExpectedDTO saved = save(dto);
        logger.debug("Version of merged: [" + saved.getVersion() + "]");
        return saved;
    }

    @BridgeOverride
    public ExpectedDTO createExpectedVersion(VersionDTO version) {
        ExpectedDTO result = find(version);
        if (result == null) {
            result = create(version);
        }
        return result;
    }

}
