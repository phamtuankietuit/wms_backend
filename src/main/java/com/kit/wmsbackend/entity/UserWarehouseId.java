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
public class UserWarehouseId implements Serializable {
    private UUID userId;
    private UUID warehouseId;
}



