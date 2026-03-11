package com.kit.wmsbackend.feature.mail.service;

import com.kit.wmsbackend.feature.mail.dto.MailDto;
import org.springframework.mail.MailException;

public interface MailService {
    void sendMail(MailDto emailData) throws MailException;
}
