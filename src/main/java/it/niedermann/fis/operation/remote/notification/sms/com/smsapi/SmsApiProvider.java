package it.niedermann.fis.operation.remote.notification.sms.com.smsapi;

import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.NotificationConfiguration;
import it.niedermann.fis.operation.remote.notification.OperationNotificationUtil;
import it.niedermann.fis.operation.remote.notification.sms.SmsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class SmsApiProvider extends SmsProvider {

    private final Logger logger = LoggerFactory.getLogger(SmsApiProvider.class);

    private final SmsApiService service = new Retrofit.Builder()
            .baseUrl("https://api.smsapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SmsApiService.class);

    public SmsApiProvider(
            NotificationConfiguration config,
            OperationNotificationUtil notificationUtil) {
        super(config, notificationUtil);
    }

    @Override
    public void accept(OperationDto operation) {
        apiKey.ifPresentOrElse(
                apiKey -> {
                    if (recipients.size() > 0) {
                        try {
                            final var response = service
                                    .sendSms("Bearer " + apiKey,
                                            senderName,
                                            String.join(",", recipients),
                                            getMessage(operation),
                                            priority ? 1 : null)
                                    .execute();
                            logger.debug("HTTP Response code: " + response.code());
                            logger.trace("HTTP Response body: " + response.body().string());
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    } else {
                        logger.trace("No recipients for SMS");
                    }
                },
                () -> this.logger.trace("✉️ Skipped sending SMS because API key has not been provided."));
    }

    /**
     * Makes use of
     * <a href="https://www.smsapi.com/docs/#messages-with-cut-li">short URL
     * service</a> for sending the maps link
     */
    @Override
    protected String getMessage(OperationDto operation) {
        final var address = notificationUtil.getHumanReadableLocation(operation);
        return address.isPresent()
                ? String.format("Einsatz: %s, %s",
                        operation.getKeyword(),
                        String.join(", ", operation.getTags()))
                : String.format("Einsatz: %s, Karte: %s, Adresse: %s",
                        operation.getKeyword(),
                        "[%goto:" + notificationUtil.getGoogleMapsLink(operation) + "%]",
                        address);
    }
}
