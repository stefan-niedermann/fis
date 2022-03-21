package it.niedermann.fis.operation.remote.notification.sms;

import it.niedermann.fis.operation.remote.notification.NotificationConfiguration;
import it.niedermann.fis.operation.remote.notification.OperationNotificationUtil;
import it.niedermann.fis.operation.remote.notification.sms.com.smsapi.SmsApiProvider;
import it.niedermann.fis.operation.remote.notification.sms.io.sms77.Sms77Provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class SmsProviderFactoryTest {

    private SmsProviderFactory factory;

    @BeforeEach()
    public void setup() {
        final var config = mock(NotificationConfiguration.class);
        factory = new SmsProviderFactory(config, mock(OperationNotificationUtil.class));
    }

    @Test
    public void shouldCreateSmsProviderDependingOnType() {
        assertEquals(SmsApiProvider.class, factory.createSmsProvider(SmsProviderType.SMSAPI).getClass());
        assertEquals(Sms77Provider.class, factory.createSmsProvider(SmsProviderType.SMS77).getClass());
    }
}
