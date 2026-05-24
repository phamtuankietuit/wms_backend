package com.kit.wmsbackend.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.sequences")
public record SequenceProperties(
        @NotBlank(message = "Stock transaction name must not be blank")
        String stockTransactionSeqName,

        @NotBlank(message = "Import prefix must not be blank")
        String importPrefix,

        @NotBlank(message = "Export prefix must not be blank")
        String exportPrefix,

        @NotBlank(message = "Adjustment prefix must not be blank")
        String adjustmentPrefix,

        @NotBlank(message = "User sequence name must not be blank")
        String userSeqName,

        @NotBlank(message = "User prefix must not be blank")
        String userPrefix,

        @NotNull(message = "Padding length must be configured")
        @Min(value = 6, message = "Padding length must be at least 6")
        Integer paddingLength
) {
}
