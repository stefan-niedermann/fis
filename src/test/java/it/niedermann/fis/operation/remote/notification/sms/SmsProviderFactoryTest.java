package it.niedermann.fis.operation.remote.notification.sms;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.operation.remote.notification.OperationNotificationUtil;
import it.niedermann.fis.operation.remote.notification.sms.sms77.Sms77Provider;
import it.niedermann.fis.operation.remote.notification.sms.smsapi.SmsApiProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SmsProviderFactoryTest {

    private SmsProviderFactory factory;

    @BeforeEach()
    public void setup() {
        final var config = mock(FisConfiguration.class);
        final var operationConfig = mock(FisConfiguration.OperationConfiguration.class);
        when(config.operation()).thenReturn(operationConfig);
        when(operationConfig.notification()).thenReturn(mock(FisConfiguration.OperationConfiguration.NotificationConfiguration.class));
        factory = new SmsProviderFactory(config, mock(OperationNotificationUtil.class));
    }

    @Test
    public void shouldCreateSmsProviderDependingOnType() {
        assertEquals(SmsApiProvider.class, factory.createSmsProvider(SmsProviderType.SMSAPI).getClass());
        assertEquals(Sms77Provider.class, factory.createSmsProvider(SmsProviderType.SMS77).getClass());
    }
}
