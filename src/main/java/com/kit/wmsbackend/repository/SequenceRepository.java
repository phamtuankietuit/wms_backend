package com.kit.wmsbackend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class SequenceRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Long getNextSequenceValue(String seqName) {
        Number value = (Number) entityManager
                .createNativeQuery("SELECT nextval(:seqName)")
                .setParameter("seqName", seqName)
                .getSingleResult();
        return value.longValue();
    }
}
