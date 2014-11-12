package org.leialearns.api.command;

import org.leialearns.command.Command;
import org.leialearns.logic.model.Expected;

public interface Consolidator extends Command {
    Expected getLastExpected();
}
