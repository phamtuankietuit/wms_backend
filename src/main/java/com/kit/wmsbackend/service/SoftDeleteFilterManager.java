package com.kit.wmsbackend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class SoftDeleteFilterManager {
    public static final String FILTER_NAME  = "softDeleteFilter";
    public static final String PARAM_INCLUDE_DELETED = "includeDeleted";

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Enable filter to show only active records (deletedAt IS NULL)
     */
    public void enableFilter() {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter(FILTER_NAME).setParameter(PARAM_INCLUDE_DELETED, false);
    }

    /**
     * Disable filter to show all records (including deleted)
     */
    public void disableFilter() {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter(FILTER_NAME).setParameter(PARAM_INCLUDE_DELETED, true);
    }

    /**
     * Execute operation with filter enabled (show only active)
     */
    public <T> T executeWithActiveFilter(@NonNull Supplier<T> operation) {
       enableFilter();
       return operation.get();
    }

    /**
     * Execute operation without filter (show all including deleted)
     */
    public <T> T executeWithoutFilter(@NonNull Supplier<T> operation) {
        try {
            disableFilter();
            return operation.get();
        } finally {
            enableFilter();
        }
    }

    /**
     * Execute void operation with filter
     */
    public void executeWithActiveFilter(@NonNull Runnable operation) {
        enableFilter();
        operation.run();
    }

    /**
     * Execute void operation without filter
     */
    public void executeWithoutFilter(@NonNull Runnable operation) {
        try {
            disableFilter();
            operation.run();
        } finally {
            enableFilter();
        }
    }
}