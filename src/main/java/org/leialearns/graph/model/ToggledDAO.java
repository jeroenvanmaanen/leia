package org.leialearns.graph.model;

import org.leialearns.enumerations.ModelType;
import org.leialearns.graph.session.SessionDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.graph.IdDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;

public class ToggledDAO extends IdDaoSupport<ToggledDTO> {

    @Autowired
    private ToggledRepository repository;

    public ToggledDTO create(SessionDTO owner, NodeDTO node, boolean include) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

    public ToggledDTO find(VersionDTO version) {
        ToggledDTO result;
        if (version != null && version.getModelType() == ModelType.TOGGLED) {
            result = repository.findByVersion(version);
        } else {
            result = null;
        }
        return null;
    }

    public boolean equals(ToggledDTO toggledDTO, Object other) {
        throw new UnsupportedOperationException("TODO: implement"); // TODO: implement
    }

}
