package it.niedermann.fis.operation.remote.notification.sms;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.OperationNotificationUtil;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Service
public abstract class AbstractSmsProvider implements Consumer<OperationDto> {

    protected final String apiKey;
    protected final Collection<String> recipients;
    private final OperationNotificationUtil notificationUtil;

    /**
     * @see <a href="https://www.baeldung.com/java-regex-validate-phone-numbers#multiple">Source at baeldung.com</a>
     */
    private final Pattern phoneNumberPattern = Pattern.compile(
            "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                    + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                    + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$");

    public AbstractSmsProvider(
            FisConfiguration config,
            OperationNotificationUtil notificationUtil
    ) {
        this.notificationUtil = notificationUtil;
        this.apiKey = config.operation().smsApiKey();
        this.recipients = filterPhoneRecipients(config.operation().recipients());
    }

    protected String getMessage(OperationDto operation) {
        return notificationUtil.getGoogleMapsLink(operation);
    }

    private Collection<String> filterPhoneRecipients(Collection<String> recipients) {
        return recipients == null
                ? Collections.emptyList()
                : recipients.stream().filter(recipient -> phoneNumberPattern.matcher(recipient).matches()).toList();
    }
}
