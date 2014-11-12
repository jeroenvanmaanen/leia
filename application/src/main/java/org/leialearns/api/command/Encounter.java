package org.leialearns.api.command;

import org.leialearns.api.model.Version;
import org.leialearns.command.Command;

public interface Encounter extends Command {
    Version getLastVersion();
}
