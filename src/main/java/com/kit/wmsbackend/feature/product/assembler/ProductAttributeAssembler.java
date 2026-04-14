package com.kit.wmsbackend.feature.product.assembler;

import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.entity.ProductAttribute;
import com.kit.wmsbackend.entity.ProductAttributeId;
import com.kit.wmsbackend.feature.product.dto.ProductCreateAttributeContext;
import org.springframework.stereotype.Component;

@Component
public class ProductAttributeAssembler {
    public ProductAttribute toProductAttribute(Product product, ProductCreateAttributeContext context) {
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setId(new ProductAttributeId(product.getId(), context.attribute().getId()));
        productAttribute.setProduct(product);
        productAttribute.setAttribute(context.attribute());
        productAttribute.setIsActive(true);
        return productAttribute;
    }
}