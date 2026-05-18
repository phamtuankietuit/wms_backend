package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import org.hibernate.Hibernate;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

/**
 * Base entity class providing ID-based equality and hashing.
 *
 * <p><strong>Design:</strong> All entities in this system use ID-based equality semantics.
 * Two entities are considered equal if they have the same {@code id}, regardless of their
 * other field values. This is the standard approach for JPA entities to maintain consistency
 * with database identity and avoid issues with lazy loading, detached entities, and proxies.
 *
 * <p><strong>Equals Contract:</strong>
 * <ul>
 *   <li>Self-equality: Returns true if objects are the same instance.</li>
 *   <li>Type checking: Uses {@link Hibernate#getClass(Object)} to correctly handle proxies.</li>
 *   <li>ID-based equality: Returns true if IDs are equal and non-null.</li>
 *   <li>Unsaved entities: Unsaved entities (with null ID) are only equal to themselves.</li>
 * </ul>
 *
 * <p><strong>Hash Code Contract:</strong> Returns the hash of the ID if present, or
 * {@link System#identityHashCode(Object)} for unsaved entities. This ensures consistency
 * with equals and prevents issues in hash-based collections like {@code HashSet} and
 * {@code HashMap}.
 *
 * <p><strong>Final Methods:</strong> Both {@code equals()} and {@code hashCode()} are
 * declared {@code final} to prevent subclasses from breaking the contract. Entity identity
 * must be consistent across the inheritance hierarchy.
 */
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

