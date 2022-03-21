package it.niedermann.fis.operation.remote.notification.sms.io.sms77;

import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.NotificationConfiguration;
import it.niedermann.fis.operation.remote.notification.OperationNotificationUtil;
import it.niedermann.fis.operation.remote.notification.sms.SmsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class Sms77Provider extends SmsProvider {

    private final Logger logger = LoggerFactory.getLogger(Sms77Provider.class);

    private final Sms77Service service = new Retrofit.Builder()
            .baseUrl("https://gateway.sms77.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Sms77Service.class);

    public Sms77Provider(
            NotificationConfiguration config,
            OperationNotificationUtil notificationUtil
    ) {
        super(config, notificationUtil);
        logger.warn("⚠️ This SMS provider has not been tested. Use at your own risk!");
    }

    @Override
    public void accept(OperationDto operation) {
        apiKey.ifPresentOrElse(
                apiKey -> recipients.forEach(recipient -> {
                    try {
                        final var response = service.sendSms(apiKey, recipient, getMessage(operation)).execute();
                        logger.debug("HTTP Response code: " + response.code());
                        logger.trace("HTTP Response body: " + response.body().string());
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }),
                () -> this.logger.trace("✉️ Skipped sending SMS because API key has not been provided.")
        );
    }
}
