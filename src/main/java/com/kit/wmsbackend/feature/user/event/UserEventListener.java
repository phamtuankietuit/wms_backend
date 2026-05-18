package com.kit.wmsbackend.feature.user.event;

import com.kit.wmsbackend.config.properties.ClientProperties;
import com.kit.wmsbackend.config.properties.JwtProperties;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.MailTemplate;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.auth.service.JwtService;
import com.kit.wmsbackend.feature.mail.dto.MailDto;
import com.kit.wmsbackend.feature.mail.service.MailService;
import com.kit.wmsbackend.feature.user.dto.UserCreatedEvent;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
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
    JwtService jwtService;
    MailService mailService;
    UserRepository userRepository;
    ClientProperties clientProperties;
    JwtProperties jwtProperties;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSendOnboardingEmail(@NonNull UserCreatedEvent event) {
        try {
            User user = userRepository.findNotDeletedById(event.userId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, event.userId().toString()));

            String resetToken = jwtService.createOnboardingResetToken(user);

            String resetLink = UriComponentsBuilder.fromUriString(clientProperties.url() + "/reset-password")
                    .queryParam("token", resetToken)
                    .build()
                    .toUriString();

            Map<String, Object> props = new HashMap<>();
            props.put("name", event.name());
            props.put("resetPasswordLink", resetLink);
            props.put("expirationMinutes", Duration.ofMillis(jwtProperties.onboardingResetExpiration()).toMinutes());

            MailDto dataMail = mailService.createMailDto(
                    event.email(),
                    MailTemplate.RESET_PASSWORD,
                    props
            );

            mailService.sendMail(dataMail);
        } catch (Exception e) {
            log.error("Failed to send onboarding email to {}", event.email(), e);
        }
    }
}
