package com.thales.common.aspect;

// ReadOnlyRouteAspect is no longer needed.
// Routing is handled directly in RoutingDataSource.determineCurrentLookupKey()
// via TransactionSynchronizationManager.isCurrentTransactionReadOnly().
// Keeping this file to avoid breaking any existing imports.
