package it.niedermann.fis.operation.remote.notification;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@SuppressWarnings("SpellCheckingInspection")
@ConfigurationProperties("fis.notification")
@Validated
public record NotificationConfiguration(
                @NotBlank String senderName,
                String senderMail,
                String smsApiKey,
                int smsLimit,
                boolean smsPriority,
                Collection<@Email String> mail,
                Collection<String> sms) {
}