package com.kit.wmsbackend.feature.mail.service;

import com.kit.wmsbackend.enums.MailTemplate;
import com.kit.wmsbackend.feature.mail.dto.MailDto;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.name}")
    String fromName;

    @Value("${spring.mail.username}")
    String fromAddress;

    @Override
    @Async
    public void sendMail(@NonNull MailDto dataMail) throws MailException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(dataMail.getProperties());

            String html = templateEngine.process(dataMail.getTemplateName(), context);

            helper.setFrom(new InternetAddress(fromAddress, fromName, StandardCharsets.UTF_8.name()));
            helper.setTo(dataMail.getTo());
            helper.setSubject(dataMail.getSubject());
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MailException e) {
            log.error("Failed to send email", e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to send email", e);
            throw new MailPreparationException("Failed to send email", e);
        }
    }

    @Override
    public MailDto createMailDto(String to, @NonNull MailTemplate template, Map<String, Object> props) {
        MailDto dataMail = new MailDto();
        dataMail.setTo(to);
        dataMail.setSubject(template.getSubject());
        dataMail.setTemplateName(template.getTemplate());
        dataMail.setProperties(props);

        return dataMail;
    }
}
