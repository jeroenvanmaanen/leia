package org.leialearns.api.command;

import org.leialearns.api.model.Version;

public interface Encounter extends Command {
    Version getLastVersion();
}
