package com.kit.wmsbackend.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserWarehouseId implements Serializable {
    private String userId;
    private String warehouseId;
}


