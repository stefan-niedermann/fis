package it.niedermann.fis.operation.remote.notification.sms;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.OperationNotificationUtil;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public abstract class AbstractSmsProvider implements Consumer<OperationDto> {

    protected final Optional<String> apiKey;
    protected final Collection<String> recipients;
    protected final String senderName;
    private final OperationNotificationUtil notificationUtil;

    public AbstractSmsProvider(
            FisConfiguration config,
            OperationNotificationUtil notificationUtil
    ) {
        this.notificationUtil = notificationUtil;
        this.apiKey = Optional.ofNullable(config.operation().notification().smsApiKey());
        this.senderName = config.operation().notification().senderName();
        this.recipients = filterPhoneRecipients(config.operation().notification().sms());
    }

    protected String getMessage(OperationDto operation) {
        return notificationUtil.getGoogleMapsLink(operation);
    }

    private Collection<String> filterPhoneRecipients(Collection<String> recipients) {
        return recipients == null
                ? Collections.emptyList()
                : recipients
                .stream()
                .filter(this::isValidPhoneNumber)
                .map(this::sanitizePhoneNumber)
                .toList();
    }

    protected boolean isValidPhoneNumber(String source) {
        return source != null && sanitizePhoneNumber(source).length() > 6;
    }

    protected String sanitizePhoneNumber(String source) {
        return source.replaceAll("\\D", "");
    }
}
