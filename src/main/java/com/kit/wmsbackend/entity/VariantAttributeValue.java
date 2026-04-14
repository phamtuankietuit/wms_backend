package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "variants_attribute_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VariantAttributeValue {
    @EqualsAndHashCode.Include
    @EmbeddedId
    VariantAttributeValueId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("variantId")
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attributeValueId")
    @JoinColumn(name = "attribute_value_id", nullable = false)
    private AttributeValue attributeValue;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private Boolean isActive = true;
}
