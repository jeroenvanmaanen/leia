package org.leialearns.graph;

import org.leialearns.utilities.TransactionHelper;

public class GraphTransactionHelper implements TransactionHelper {

    @Override
    public void runInTransaction(Runnable runnable) {
        runnable.run(); // TODO: implement
    }

}
