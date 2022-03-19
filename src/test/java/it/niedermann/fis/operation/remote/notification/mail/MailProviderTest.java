package it.niedermann.fis.operation.remote.notification.mail;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.OperationNotificationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class MailProviderTest {

    private MailProvider repository;
    private JavaMailSender mailSender;
    private FisConfiguration config;
    private FisConfiguration.OperationConfiguration.NotificationConfiguration notificationConfig;

    @BeforeEach()
    public void setup() {
        config = mock(FisConfiguration.class);
        notificationConfig = mock(FisConfiguration.OperationConfiguration.NotificationConfiguration.class);
        final var operationConfig = mock(FisConfiguration.OperationConfiguration.class);
        when(operationConfig.notification()).thenReturn(notificationConfig);
        when(config.operation()).thenReturn(operationConfig);
    }

    @Test
    public void givenSmtpAndRecipientsAreConfigured() {
        mailSender = mock(JavaMailSender.class);
        when(notificationConfig.mail()).thenReturn(List.of("foo@example.com", "bar@example.com"));

        repository = new MailProvider(config, mock(OperationNotificationUtil.class), Optional.of(mailSender), Optional.empty());
        repository.accept(mock(OperationDto.class));

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class), any(SimpleMailMessage.class));
    }

    @Test
    public void givenOnlyRecipientsAreConfigured() {
        mailSender = mock(JavaMailSender.class);
        when(notificationConfig.mail()).thenReturn(List.of("foo@example.com", "bar@example.com"));

        repository = new MailProvider(config, mock(OperationNotificationUtil.class), Optional.empty(), Optional.empty());
        repository.accept(mock(OperationDto.class));

        verifyNoInteractions(mailSender);
    }

    @Test
    public void givenNoRecipientIsConfigured() {
        mailSender = mock(JavaMailSender.class);
        when(notificationConfig.mail()).thenReturn(null);
        when(notificationConfig.senderName()).thenReturn("JarFIS");

        repository = new MailProvider(config, mock(OperationNotificationUtil.class), Optional.of(mailSender), Optional.empty());
        repository.accept(mock(OperationDto.class));

        verifyNoInteractions(mailSender);
    }

    @Test
    public void shouldUseSenderPropertyAsFromIfPresent() {
        mailSender = mock(JavaMailSender.class);
        when(notificationConfig.mail()).thenReturn(Collections.singletonList("foo@example.com"));
        when(notificationConfig.senderName()).thenReturn("Bar");
        when(notificationConfig.senderMail()).thenReturn("bar@example.com");

        repository = new MailProvider(config, mock(OperationNotificationUtil.class), Optional.of(mailSender), Optional.empty());
        repository.accept(mock(OperationDto.class));

        verify(mailSender, times(1)).send(
                (SimpleMailMessage[]) argThat(message -> "Bar <bar@example.com>".equals(((SimpleMailMessage) message).getFrom()))
        );
    }

    @Test
    public void shouldUseFallbackIfSenderPropertyIsUnset() {
        mailSender = mock(JavaMailSender.class);
        when(notificationConfig.mail()).thenReturn(Collections.singletonList("foo@example.com"));
        when(notificationConfig.senderName()).thenReturn("JarFIS");

        repository = new MailProvider(config, mock(OperationNotificationUtil.class), Optional.of(mailSender), Optional.empty());
        repository.accept(mock(OperationDto.class));

        verify(mailSender, times(1)).send(
                (SimpleMailMessage[]) argThat(messages -> "JarFIS".equals(((SimpleMailMessage) messages).getFrom()))
        );
    }

    @Test
    public void shouldHandleFailuresGracefully() {
        mailSender = mock(JavaMailSender.class);
        doThrow(new MailSendException(Collections.emptyMap())).when(mailSender).send((SimpleMailMessage[]) argThat(messages -> true));
        when(notificationConfig.mail()).thenReturn(Collections.singletonList("foo@example.com"));

        repository = new MailProvider(config, mock(OperationNotificationUtil.class), Optional.of(mailSender), Optional.empty());

        try {
            repository.accept(mock(OperationDto.class));
        } catch (Exception e) {
            fail(e);
        }
    }

}
