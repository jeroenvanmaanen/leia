package org.leialearns.api.command;

import org.leialearns.api.model.Expected;

public interface Consolidator extends Command {
    Expected getLastExpected();
}
