package com.kit.wmsbackend.feature.attribute.specificationbuilder;

import com.kit.wmsbackend.entity.Attribute;
import com.kit.wmsbackend.entity.Attribute_;
import com.kit.wmsbackend.entity.BaseEntity_;
import com.kit.wmsbackend.specification.BaseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttributeSpecificationBuilder {
    public Specification<Attribute> notDeletedAndActiveByIds(Collection<UUID> ids) {
        return Specification.allOf(
                BaseSpecification.notDeleted(),
                BaseSpecification.isTrue(Attribute_.isActive),
                BaseSpecification.fieldIn(BaseEntity_.id, ids)
        );
    }
}
