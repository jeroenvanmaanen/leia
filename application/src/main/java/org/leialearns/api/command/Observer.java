package org.leialearns.api.command;

import org.leialearns.api.model.Observed;
import org.leialearns.command.Command;

public interface Observer extends Command {
    Observed getLastCreated();
}
