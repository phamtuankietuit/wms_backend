package com.kit.wmsbackend.feature.mail.service;

import com.kit.wmsbackend.config.properties.MailProperties;
import com.kit.wmsbackend.enums.MailTemplate;
import com.kit.wmsbackend.feature.mail.dto.MailDto;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailServiceImpl implements MailService {
    JavaMailSender mailSender;
    TemplateEngine templateEngine;
    MailProperties mailProperties;

    @Override
    @Async
    public void sendMail(@NonNull MailDto dataMail) throws MailException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(dataMail.getProperties());

            String html = templateEngine.process(dataMail.getTemplateName(), context);

            helper.setFrom(new InternetAddress(mailProperties.username(), mailProperties.name(), StandardCharsets.UTF_8.name()));
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
