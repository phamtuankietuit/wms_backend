package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "variants")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Variant extends BaseAuditEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private Boolean isActive = true;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE", nullable = false)
    private Boolean isDefault = false;

    @OneToMany(mappedBy = "variant")
    @EqualsAndHashCode.Exclude
    private List<Inventory> inventories = new ArrayList<>();

    @OneToMany(mappedBy = "variant")
    @EqualsAndHashCode.Exclude
    private List<StockTransactionItem> stockTransactionItems = new ArrayList<>();

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<VariantAttributeValue> variantAttributeValues = new ArrayList<>();

    public void addVariantAttributeValue(VariantAttributeValue variantAttributeValue) {
        Objects.requireNonNull(variantAttributeValue, "variantAttributeValue must not be null");
        variantAttributeValues.add(variantAttributeValue);
        variantAttributeValue.setVariant(this);
    }

    public void removeVariantAttributeValue(VariantAttributeValue variantAttributeValue) {
        if (variantAttributeValue == null) {
            return;
        }

        variantAttributeValues.remove(variantAttributeValue);
        variantAttributeValue.setVariant(null);
    }
}
