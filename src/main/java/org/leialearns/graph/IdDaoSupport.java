package org.leialearns.graph;

import org.leialearns.bridge.FarObject;

public class IdDaoSupport<DTO extends HasId & FarObject<?>> {

    public static String toID(String label, HasId object) {
        return "???"; // TODO: implement
    }

    protected Object adapt(Object object) {
        return null; // TODO: implement
    }

}
