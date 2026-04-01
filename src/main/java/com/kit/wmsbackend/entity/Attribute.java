package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attributes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Attribute extends BaseAuditEntity{
    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<AttributeValue> attributeValues = new ArrayList<>();

    public void addAttributeValue(AttributeValue attributeValue) {
        attributeValues.add(attributeValue);
        attributeValue.setAttribute(this);
    }

    public void removeAttributeValue(AttributeValue attributeValue) {
        attributeValues.remove(attributeValue);
        attributeValue.setAttribute(null);
    }
}
