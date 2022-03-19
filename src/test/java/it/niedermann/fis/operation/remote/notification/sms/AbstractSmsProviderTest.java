package it.niedermann.fis.operation.remote.notification.sms;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.OperationNotificationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractSmsProviderTest {

    private AbstractSmsProvider provider;
    private FisConfiguration config;
    private FisConfiguration.OperationConfiguration.NotificationConfiguration notificationConfig;

    @BeforeEach()
    public void setup() {
        config = mock(FisConfiguration.class);
        final var operationConfig = mock(FisConfiguration.OperationConfiguration.class);
        notificationConfig = mock(FisConfiguration.OperationConfiguration.NotificationConfiguration.class);
        when(config.operation()).thenReturn(operationConfig);
        when(operationConfig.notification()).thenReturn(notificationConfig);
    }

    @Test
    public void shouldHandleNotConfiguredRecipients() {
        when(notificationConfig.sms()).thenReturn(null);
        provider = new AbstractSmsProvider(config, mock(OperationNotificationUtil.class)) {
            @Override
            public void accept(OperationDto operationDto) {
                assertEquals(0, recipients.size());
            }
        };
        provider.accept(mock(OperationDto.class));
    }

    @Test
    public void shouldFilterInvalidPhoneNumbers() {
        when(notificationConfig.sms()).thenReturn(List.of("2055550125", "foobar", "123"));
        provider = new AbstractSmsProvider(config, mock(OperationNotificationUtil.class)) {
            @Override
            public void accept(OperationDto operationDto) {
                assertEquals(1, recipients.size());
            }
        };
        provider.accept(mock(OperationDto.class));
    }
}
