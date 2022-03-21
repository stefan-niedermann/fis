package it.niedermann.fis.operation.remote.notification;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.mail.MailProvider;
import it.niedermann.fis.operation.remote.notification.sms.SmsProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

import static it.niedermann.fis.operation.remote.notification.sms.SmsProviderType.SMSAPI;

@Service
public class OperationNotificationRepositoryImpl implements OperationNotificationRepository {

    private final Logger logger = LoggerFactory.getLogger(OperationNotificationRepositoryImpl.class);

    private final Consumer<OperationDto> mailProvider;
    private final Consumer<OperationDto> smsProvider;
    private final int smsLimit;
    private int smsCount = 0;

    public OperationNotificationRepositoryImpl(
            FisConfiguration config,
            MailProvider mailProvider,
            SmsProviderFactory smsProviderFactory
    ) {
        this.smsLimit = config.operation().notification().smsLimit();
        if (smsLimit <= 0) {
            this.logger.warn("Consider defining a limit for SMS notifications to avoid high costs by spammers.");
        }
        this.mailProvider = mailProvider;
        this.smsProvider = smsProviderFactory.createSmsProvider(SMSAPI);
    }

    @Async
    @Override
    public void accept(OperationDto operation) {
        if (smsLimit > 0 && smsCount < smsLimit) {
            this.smsProvider.accept(operation);
            this.smsCount++;
        } else {
            this.logger.warn("Skipped sending SMS notification because daily limit is exceeded");
        }

        this.mailProvider.accept(operation);
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Override
    public void resetLimits() {
        this.smsCount = 0;
    }
}
