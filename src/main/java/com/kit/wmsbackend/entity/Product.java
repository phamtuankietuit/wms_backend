package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseAuditEntity {
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<Variant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @EqualsAndHashCode.Exclude
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    public void addVariant(Variant variant) {
        Objects.requireNonNull(variant, "variant must not be null");
        variants.add(variant);
        variant.setProduct(this);
    }

    public void removeVariant(Variant variant) {
        if (variant == null) {
            return;
        }

        variants.remove(variant);
        variant.setProduct(null);
    }

    public void addProductAttribute(ProductAttribute productAttribute) {
        Objects.requireNonNull(productAttribute, "productAttribute must not be null");
        productAttributes.add(productAttribute);
        productAttribute.setProduct(this);
    }

    public void removeProductAttribute(ProductAttribute productAttribute) {
        if (productAttribute == null) {
            return;
        }

        productAttributes.remove(productAttribute);
        productAttribute.setProduct(null);
    }
}
