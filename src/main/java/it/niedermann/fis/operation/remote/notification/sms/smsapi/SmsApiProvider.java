package it.niedermann.fis.operation.remote.notification.sms.smsapi;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.OperationNotificationUtil;
import it.niedermann.fis.operation.remote.notification.sms.AbstractSmsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class SmsApiProvider extends AbstractSmsProvider {

    private final Logger logger = LoggerFactory.getLogger(SmsApiProvider.class);

    private final SmsApiService service = new Retrofit.Builder()
            .baseUrl("https://api.smsapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SmsApiService.class);

    public SmsApiProvider(
            FisConfiguration config,
            OperationNotificationUtil notificationUtil
    ) {
        super(config, notificationUtil);
    }

    @Override
    public void accept(OperationDto operation) {
        apiKey.ifPresentOrElse(
                apiKey -> {
                    if (recipients.size() > 0) {
                        try {
                            final Response<String> response = service.sendSms(apiKey, senderName, String.join(",", recipients), getMessage(operation)).execute();
                            logger.debug(response.body());
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                },
                () -> this.logger.trace("✉️ Skipped sending SMS because API key has not been provided.")
        );
    }
}
