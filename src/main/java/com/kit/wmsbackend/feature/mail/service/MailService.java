package com.kit.wmsbackend.feature.mail.service;

import com.kit.wmsbackend.enums.MailTemplate;
import com.kit.wmsbackend.feature.mail.dto.MailDto;
import org.springframework.mail.MailException;

import java.util.Map;

public interface MailService {
    void sendMail(MailDto emailData) throws MailException;
    MailDto createMailDto(String to, MailTemplate template, Map<String, Object> props);
}
