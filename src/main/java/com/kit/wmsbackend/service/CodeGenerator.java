package com.kit.wmsbackend.service;

import com.kit.wmsbackend.config.properties.SequenceProperties;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.SequenceType;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.repository.SequenceRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CodeGenerator {
    private final SequenceRepository sequenceRepository;
    private final SequenceProperties sequenceProperties;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy");

    public String generateForStockTransaction(SequenceType type) {
        if (type == null) {
            throw new AppException(ErrorCode.CODE_GENERATION_FAILED, "Sequence type must not be null");
        }

        String prefix = resolvePrefix(type);
        Long sequenceValue = sequenceRepository.getNextSequenceValue(sequenceProperties.stockTransactionSeqName());
        return generate(prefix, sequenceValue);
    }

    public String generateForUser() {
        String prefix = sequenceProperties.userPrefix();
        Long sequenceValue = sequenceRepository.getNextSequenceValue(sequenceProperties.userSeqName());
        return generate(prefix, sequenceValue);
    }

    private @NonNull String generate(
            String prefix,
            Long sequenceValue
    ) {
        validatePrefix(prefix);
        validateSequence(sequenceValue);

        String datePart = LocalDate.now().format(DATE_FORMAT);
        String sequencePattern = "%%1$0%dd".formatted(sequenceProperties.paddingLength());
        String sequencePart = String.format(sequencePattern, sequenceValue);

        return String.format("%s-%s-%s", prefix.toUpperCase(Locale.ROOT), datePart, sequencePart);
    }

    private String resolvePrefix(@NonNull SequenceType type) {
        return switch (type) {
            case IMPORT -> sequenceProperties.importPrefix();
            case EXPORT -> sequenceProperties.exportPrefix();
            case ADJUSTMENT -> sequenceProperties.adjustmentPrefix();
        };
    }

    private void validatePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            throw new AppException(
                    ErrorCode.CODE_GENERATION_FAILED,
                    "Prefix must not be blank"
            );
        }
    }

    private void validateSequence(Long sequenceValue) {
        if (sequenceValue == null || sequenceValue <= 0) {
            throw new AppException(
                    ErrorCode.CODE_GENERATION_FAILED,
                    "Sequence value must be greater than zero"
            );
        }
    }
}
