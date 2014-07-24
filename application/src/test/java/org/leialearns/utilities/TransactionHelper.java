package org.leialearns.utilities;

public interface TransactionHelper {
    void runInTransaction(Runnable runnable);
}
