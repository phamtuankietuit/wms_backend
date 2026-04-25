package com.kit.wmsbackend.annotation;

import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.enums.PermissionMatchMode;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@PreAuthorize("@authz.hasByMode('{value}', '{mode}')")
public @interface RequirePermission {
    PermissionCode[] value();

    @SuppressWarnings("unused")
    PermissionMatchMode mode() default PermissionMatchMode.ALL;
}
