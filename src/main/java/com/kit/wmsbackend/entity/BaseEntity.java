package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import org.hibernate.Hibernate;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BaseEntity that)) {
            return false;
        }

        if (Hibernate.getClass(this) != Hibernate.getClass(that)) {
            return false;
        }

        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + id + ")";
    }
}

