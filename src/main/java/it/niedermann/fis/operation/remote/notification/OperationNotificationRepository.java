package it.niedermann.fis.operation.remote.notification;

import it.niedermann.fis.main.model.OperationDto;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public interface OperationNotificationRepository extends Consumer<OperationDto> {

    /**
     * Sending notifications can be time intensive and cost money (for example for sending SMS).
     * This method can take care of resetting those limits, for example with a cron job.
     */
    void resetLimits();
}
