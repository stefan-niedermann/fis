package it.niedermann.fis.operation.remote.mail;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
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

public class OperationMailRepositoryTest {

    private OperationMailRepository repository;
    private JavaMailSender mailSender;
    private FisConfiguration config;
    private FisConfiguration.OperationConfiguration operationConfig;

    @BeforeEach()
    public void setup() {
        config = mock(FisConfiguration.class);
        operationConfig = mock(FisConfiguration.OperationConfiguration.class);
        when(config.operation()).thenReturn(operationConfig);
    }

    @Test
    public void givenSmtpAndRecipientsAreConfigured() {
        mailSender = mock(JavaMailSender.class);
        when(operationConfig.recipients()).thenReturn(List.of("foo@example.com", "bar@example.com"));

        repository = new OperationMailRepository(config, Optional.of(mailSender));
        repository.send(mock(OperationDto.class));

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class), any(SimpleMailMessage.class));
    }

    @Test
    public void givenOnlyRecipientsAreConfigured() {
        mailSender = mock(JavaMailSender.class);
        when(operationConfig.recipients()).thenReturn(List.of("foo@example.com", "bar@example.com"));

        repository = new OperationMailRepository(config, Optional.empty());
        repository.send(mock(OperationDto.class));

        verifyNoInteractions(mailSender);
    }

    @Test
    public void givenOnlySmtpIsConfigured() {
        mailSender = mock(JavaMailSender.class);
        when(operationConfig.recipients()).thenReturn(null);

        repository = new OperationMailRepository(config, Optional.of(mailSender));
        repository.send(mock(OperationDto.class));

        verifyNoInteractions(mailSender);
    }

    @Test
    public void shouldUseSenderPropertyAsFromIfPresent() {
        mailSender = mock(JavaMailSender.class);
        when(operationConfig.recipients()).thenReturn(Collections.singletonList("foo@example.com"));
        when(operationConfig.sender()).thenReturn("Foo <foo@example.com>");

        repository = new OperationMailRepository(config, Optional.of(mailSender));
        repository.send(mock(OperationDto.class));

        verify(mailSender, times(1)).send(
                (SimpleMailMessage[]) argThat(message -> "Foo <foo@example.com>".equals(((SimpleMailMessage) message).getFrom()))
        );
    }

    @Test
    public void shouldUseFallbackIfSenderPropertyIsUnset() {
        mailSender = mock(JavaMailSender.class);
        when(operationConfig.recipients()).thenReturn(Collections.singletonList("foo@example.com"));

        repository = new OperationMailRepository(config, Optional.of(mailSender));
        repository.send(mock(OperationDto.class));

        verify(mailSender, times(1)).send(
                (SimpleMailMessage[]) argThat(messages -> "JarFIS".equals(((SimpleMailMessage) messages).getFrom()))
        );
    }

    @Test
    public void shouldHandleFailuresGracefully() {
        mailSender = mock(JavaMailSender.class);
        doThrow(new MailSendException(Collections.emptyMap())).when(mailSender).send((SimpleMailMessage[]) argThat(messages -> true));
        when(operationConfig.recipients()).thenReturn(Collections.singletonList("foo@example.com"));

        repository = new OperationMailRepository(config, Optional.of(mailSender));

        try {
            repository.send(mock(OperationDto.class));
        } catch (Exception e) {
            fail(e);
        }
    }

}
