package org.leialearns.api.command;

import org.leialearns.command.Command;
import org.leialearns.logic.model.Observed;

public interface Observer extends Command {
    Observed getLastCreated();
}
