package it.niedermann.fis.operation.remote.notification.sms;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.OperationNotificationUtil;
import it.niedermann.fis.operation.remote.notification.sms.sms77.Sms77Provider;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class SmsProviderFactory {

    private final FisConfiguration config;
    private final OperationNotificationUtil notificationUtil;

    public SmsProviderFactory(
            FisConfiguration config,
            OperationNotificationUtil notificationUtil
    ) {
        this.config = config;
        this.notificationUtil = notificationUtil;
    }

    @SuppressWarnings({"SwitchStatementWithTooFewBranches", "UnnecessaryDefault"})
    public Consumer<OperationDto> createSmsProvider(SmsProviderType type) {
        return switch (type) {
            case SMS77 -> new Sms77Provider(config, notificationUtil);
            default -> throw new IllegalArgumentException("Could not find a " + AbstractSmsProvider.class.getSimpleName() + " for type \"" + type + "\"");
        };
    }
}
