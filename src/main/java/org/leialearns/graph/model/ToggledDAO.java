package org.leialearns.graph.model;

import org.leialearns.graph.session.SessionDTO;
import org.leialearns.graph.structure.NodeDTO;
import org.leialearns.graph.IdDaoSupport;

public class ToggledDAO extends IdDaoSupport<ToggledDTO> {

    public ToggledDTO create(SessionDTO owner, NodeDTO node, boolean include) {
        return null; // TODO: implement
    }

    public ToggledDTO find(VersionDTO version) {
        return null; // TODO: implement
    }

    public boolean equals(ToggledDTO toggledDTO, Object other) {
        return false; // TODO: implement
    }

}
