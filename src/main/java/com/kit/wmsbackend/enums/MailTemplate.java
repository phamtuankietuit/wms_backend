package com.kit.wmsbackend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailTemplate {
    RESET_PASSWORD("[WMS] Reset Your Password", "password-reset-email");

    private final String subject;
    private final String template;
}