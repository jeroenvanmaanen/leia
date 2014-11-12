package org.leialearns.api.command;

import org.leialearns.api.model.Expected;
import org.leialearns.command.Command;

public interface Consolidator extends Command {
    Expected getLastExpected();
}
