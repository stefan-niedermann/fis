package it.niedermann.fis.operation.remote.notification;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.mail.MailProvider;
import it.niedermann.fis.operation.remote.notification.sms.SmsProvider;
import it.niedermann.fis.operation.remote.notification.sms.SmsProviderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class OperationNotificationRepositoryImplTest {

    private OperationNotificationRepository repository;
    private MailProvider mailProvider;
    private SmsProvider smsProvider;
    private FisConfiguration.OperationConfiguration.NotificationConfiguration notificationConfig;

    @BeforeEach()
    public void setup() {
        mailProvider = mock(MailProvider.class);
        smsProvider = mock(SmsProvider.class);
        final var smsProviderFactory = mock(SmsProviderFactory.class);
        when(smsProviderFactory.createSmsProvider(any())).thenReturn(smsProvider);
        final var config = mock(FisConfiguration.class);
        final var operationConfig = mock(FisConfiguration.OperationConfiguration.class);
        notificationConfig = mock(FisConfiguration.OperationConfiguration.NotificationConfiguration.class);
        when(config.operation()).thenReturn(operationConfig);
        when(operationConfig.notification()).thenReturn(notificationConfig);
        when(notificationConfig.smsLimit()).thenReturn(10);
        repository = new OperationNotificationRepositoryImpl(config, mailProvider, smsProviderFactory);
    }

    @Test
    public void shouldForwardOperationsToMailAndSmsProviders() {
        final var operation = mock(OperationDto.class);

        repository.accept(operation);

        verify(mailProvider, times(1)).accept(operation);
        verify(smsProvider, times(1)).accept(operation);
    }

    @Test
    public void shouldSkipSendingSmsWhenLimitExceeds() {
        final var operation = mock(OperationDto.class);

        for (int i = 0; i < notificationConfig.smsLimit() + 1; i++) {
            repository.accept(operation);
        }

        verify(mailProvider, times(notificationConfig.smsLimit() + 1)).accept(operation);
        verify(smsProvider, times(notificationConfig.smsLimit())).accept(operation);
    }

    @Test
    public void shouldContinueSendingSmsAfterLimitWasReset() {
        final var operation = mock(OperationDto.class);

        for (int i = 0; i < notificationConfig.smsLimit() + 1; i++) {
            repository.accept(operation);
        }

        repository.resetLimits();
        repository.accept(operation);

        verify(mailProvider, times(notificationConfig.smsLimit() + 2)).accept(operation);
        verify(smsProvider, times(notificationConfig.smsLimit() + 1)).accept(operation);
    }
}
