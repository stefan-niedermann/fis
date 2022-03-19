package it.niedermann.fis.operation.remote.notification;

import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.mail.MailProvider;
import it.niedermann.fis.operation.remote.notification.sms.SmsProviderFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

import static it.niedermann.fis.operation.remote.notification.sms.SmsProviderType.SMS77;

@Service
public class OperationNotificationRepository {

    private final Consumer<OperationDto> mailProvider;
    private final Consumer<OperationDto> smsProvider;

    public OperationNotificationRepository(
            MailProvider mailProvider,
            SmsProviderFactory smsProviderFactory
    ) {
        this.mailProvider = mailProvider;
        this.smsProvider = smsProviderFactory.createSmsProvider(SMS77);
    }

    @Async
    public void notify(OperationDto operation) {
        this.smsProvider.accept(operation);
        this.mailProvider.accept(operation);
    }
}
