package org.leialearns.graph;

import org.leialearns.spring.test.TransactionHelper;
import org.springframework.transaction.annotation.Transactional;

@Transactional("neo4jTransactionManager")
public class GraphTransactionHelper implements TransactionHelper {

    @Override
    public void runInTransaction(Runnable runnable) {
        runnable.run(); // TODO: add transaction
    }

}
