package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "attribute_values", uniqueConstraints = {
        @UniqueConstraint(
                name = "uq_attribute_values",
                columnNames = {"code", "attribute_id"}
        )
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttributeValue extends BaseAuditEntity {
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @Column(nullable = false, length = 100)
    private String code;

    @Column(nullable = false, length = 100)
    private String value;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "attributeValue", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VariantAttributeValue> variantAttributeValues = new HashSet<>();
}
