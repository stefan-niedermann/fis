package it.niedermann.fis.operation.remote.notification;

import it.niedermann.fis.main.model.OperationDto;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static java.net.URLEncoder.encode;

@Service
public class OperationNotificationUtil {

    public String getGoogleMapsLink(OperationDto operation) {
        return getGoogleMapsLink(operation, null);
    }

    public String getGoogleMapsLink(OperationDto operation, String origin) {
        final var link = String.format("https://www.google.com/maps/dir/?api=1&dir_action=navigate&travelmode=driving&destination=%s",
                encode(operation.getStreet() + " " + operation.getNumber() + ", " + operation.getLocation(), StandardCharsets.UTF_8));
        return Optional.ofNullable(origin)
                .map(s -> link + "&origin=" + encode(s, StandardCharsets.UTF_8))
                .orElse(link);
    }
}
