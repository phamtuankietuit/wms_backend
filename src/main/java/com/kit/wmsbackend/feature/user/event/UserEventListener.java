package com.kit.wmsbackend.feature.user.event;

import com.kit.wmsbackend.config.properties.ClientProperties;
import com.kit.wmsbackend.config.properties.JwtProperties;
import com.kit.wmsbackend.enums.MailTemplate;
import com.kit.wmsbackend.feature.mail.dto.MailDto;
import com.kit.wmsbackend.feature.mail.service.MailService;
import com.kit.wmsbackend.feature.user.dto.UserCreatedEvent;
import com.kit.wmsbackend.feature.user.dto.UserResetPasswordEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserEventListener {
    MailService mailService;
    ClientProperties clientProperties;
    JwtProperties jwtProperties;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSendOnboardingEmail(@NonNull UserCreatedEvent event) {
        String msg = "Failed to send onboarding email to";
        sendResetPasswordEmail(event.email(), event.name(), event.resetToken(), msg, jwtProperties.onboardingResetExpiration());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSendResetPasswordEmail(@NonNull UserResetPasswordEvent event) {
        String msg = "Failed to send reset password email to";
        sendResetPasswordEmail(event.email(), event.name(), event.resetToken(), msg, jwtProperties.resetExpiration());
    }

    private void sendResetPasswordEmail(String email, String name, String resetToken, String msg, long duration) {
        try {
            String resetLink = UriComponentsBuilder.fromUriString(clientProperties.url() + "/reset-password")
                    .queryParam("token", resetToken)
                    .build()
                    .toUriString();

            Map<String, Object> props = new HashMap<>();
            props.put("name", name);
            props.put("resetPasswordLink", resetLink);
            props.put("expirationMinutes", Duration.ofMillis(duration).toMinutes());

            MailDto dataMail = mailService.createMailDto(
                    email,
                    MailTemplate.RESET_PASSWORD,
                    props
            );

            mailService.sendMail(dataMail);
        } catch (Exception e) {
            log.error("{} {}", msg, email, e);
        }
    }
}
