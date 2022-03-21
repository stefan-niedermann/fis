package it.niedermann.fis.operation.remote.notification.sms;

import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.NotificationConfiguration;
import it.niedermann.fis.operation.remote.notification.OperationNotificationUtil;
import it.niedermann.fis.operation.remote.notification.sms.com.smsapi.SmsApiProvider;
import it.niedermann.fis.operation.remote.notification.sms.io.sms77.Sms77Provider;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class SmsProviderFactory {

    private final NotificationConfiguration config;
    private final OperationNotificationUtil notificationUtil;

    public SmsProviderFactory(
            NotificationConfiguration config,
            OperationNotificationUtil notificationUtil
    ) {
        this.config = config;
        this.notificationUtil = notificationUtil;
    }

    @SuppressWarnings({"UnnecessaryDefault"})
    public Consumer<OperationDto> createSmsProvider(SmsProviderType type) {
        return switch (type) {
            case SMS77 -> new Sms77Provider(config, notificationUtil);
            case SMSAPI -> new SmsApiProvider(config, notificationUtil);
            default -> throw new NotImplementedException("Could not find a " + SmsProvider.class.getSimpleName() + " for type \"" + type + "\"");
        };
    }
}
