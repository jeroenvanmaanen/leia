package org.leialearns.api.command;

import org.leialearns.command.Command;
import org.leialearns.logic.model.Version;

public interface Encounter extends Command {
    Version getLastVersion();
}
