package org.leialearns.api.command;

import org.leialearns.api.model.Observed;

public interface Observer extends Command {
    Observed getLastCreated();
}
