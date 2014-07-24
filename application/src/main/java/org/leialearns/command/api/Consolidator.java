package org.leialearns.command.api;

import org.leialearns.command.Command;
import org.leialearns.logic.model.Expected;

public interface Consolidator extends Command {
    Expected getLastExpected();
}
