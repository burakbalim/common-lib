package com.thales.common.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // Called when LazyConnectionDataSourceProxy acquires the real connection.
        // At that point, TransactionSynchronizationManager reflects the ACTIVE
        // (outermost) transaction's readOnly flag — not any nested annotation.
        // This correctly handles nested @Transactional(readOnly=true) calls inside
        // a write transaction (they join the outer transaction and stay on PRIMARY).
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        DataSourceType type = readOnly ? DataSourceType.REPLICA : DataSourceType.PRIMARY;
        log.trace("Routing DB request to: {} (readOnly={})", type, readOnly);
        return type;
    }
}
