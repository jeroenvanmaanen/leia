package org.leialearns.api.enumerations;

/**
 * Defines the possible values for the <code>agentMode</code> argument of the <code>register</code> methods of a
 * {@link org.leialearns.api.model.Version}.
 */
@SuppressWarnings("unused")
public enum AgentMode {

    /**
     * Indicates that the agent is actively using the version.
     */
    ACTIVE,

    /**
     * Indicates that the agent is passive and is not using the version.
     */
    PASSIVE
}
