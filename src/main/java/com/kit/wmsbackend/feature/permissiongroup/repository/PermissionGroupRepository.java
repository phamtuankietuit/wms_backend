package com.kit.wmsbackend.feature.permissiongroup.repository;

import com.kit.wmsbackend.entity.PermissionGroup;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PermissionGroupRepository extends BaseAuditRepository<PermissionGroup> {
    @Query("""
            SELECT DISTINCT pg
            FROM PermissionGroup pg
            LEFT JOIN FETCH pg.permissions
            WHERE pg.id IN :ids
            """)
    List<PermissionGroup> findAllWithPermissionsByIdIn(@Param("ids") List<UUID> ids);

}

