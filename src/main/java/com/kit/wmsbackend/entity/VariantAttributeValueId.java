package com.kit.wmsbackend.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VariantAttributeValueId implements Serializable {
    private UUID variantId;
    private UUID attributeValueId;
}



