package com.kit.wmsbackend.enums;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.Collection;

public enum FilterOperator {
        EQ {
            @Override
            public Predicate apply(CriteriaBuilder cb, Path<?> path, Object value) {
                return cb.equal(path, value);
            }
        },
        LIKE {
            @Override
            public Predicate apply(CriteriaBuilder cb, Path<?> path, Object value) {
                return cb.like(
                        cb.lower(path.as(String.class)),
                        "%" + value.toString().toLowerCase() + "%"
                );
            }
        },
        GT {
            @Override
            public Predicate apply(CriteriaBuilder cb, Path<?> path, Object value) {
                return cb.greaterThan(path.as(Comparable.class), (Comparable) value);
            }
        },
        LT {
            @Override
            public Predicate apply(CriteriaBuilder cb, Path<?> path, Object value) {
                return cb.lessThan(path.as(Comparable.class), (Comparable) value);
            }
        },
        IN {
            @Override
            public Predicate apply(CriteriaBuilder cb, Path<?> path, Object value) {
                return path.in((Collection<?>) value);
            }
        };

        public abstract Predicate apply(CriteriaBuilder cb, Path<?> path, Object value);
}
