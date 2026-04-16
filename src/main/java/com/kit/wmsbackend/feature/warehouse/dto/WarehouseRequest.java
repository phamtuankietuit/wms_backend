package com.kit.wmsbackend.feature.warehouse.dto;

import com.kit.wmsbackend.constant.RegexConstant;
import jakarta.validation.constraints.*;

public record WarehouseRequest(
        @NotBlank(message = "Code is required")
        String code,

        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "isActive is required")
        Boolean isActive,

        @Size(max = 255, message = "Address must be at most 255 characters")
        String address,

        @Pattern(
                regexp = RegexConstant.PHONE_REGEX,
                message = "Phone number is not valid"
        )
        String phone,

        @Email(regexp = RegexConstant.EMAIL_REGEX, message = "Email is not valid")
        @Size(max = 100, message = "Email must be at most 100 characters")
        String email
) {
}
