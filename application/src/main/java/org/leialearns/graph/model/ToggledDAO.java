package org.leialearns.graph.model;

import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.session.SessionDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.graph.IdDaoSupport;
import org.leialearns.logic.model.Toggled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.leialearns.bridge.Static.getFarObject;
import static org.leialearns.utilities.Static.getLoggingClass;

public class ToggledDAO extends IdDaoSupport<ToggledDTO> {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private ToggledRepository repository;

    @Autowired
    private VersionDAO versionDAO;

    @Override
    protected ToggledRepository getRepository() {
        return repository;
    }

    public ToggledDTO create(SessionDTO owner, NodeDTO node, boolean include) {
        VersionDTO versionDTO = versionDAO.createVersion(owner, ModelType.TOGGLED);
        ToggledDTO dto = new ToggledDTO();
        dto.setVersion(versionDTO);
        dto.setNode(node);
        dto.setInclude(include);
        dto = save(dto);
        logger.debug("Created toggled version for: " + versionDTO.getId() + ": [" + (include ? '+' : '-') + "] " + node);
        return dto;
    }

    public ToggledDTO find(VersionDTO version) {
        ToggledDTO result;
        if (version != null && version.getModelType() == ModelType.TOGGLED) {
            result = repository.findByVersion(version);
        } else {
            result = null;
        }
        return result;
    }

    public boolean equals(ToggledDTO toggled, Object other) {
        Object otherObject = (other instanceof Toggled ? getFarObject((Toggled) other, ToggledDTO.class) : other);
        return toggled.equals(otherObject);
    }

}
