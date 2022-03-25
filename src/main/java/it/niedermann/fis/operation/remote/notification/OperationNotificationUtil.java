package it.niedermann.fis.operation.remote.notification;

import it.niedermann.fis.main.model.OperationDto;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static java.net.URLEncoder.encode;

@Service
public class OperationNotificationUtil {

    public Optional<String> getHumanReadableLocation(OperationDto operation) {
        final var street = operation.getStreet();
        final var number = operation.getNumber();
        final var location = operation.getLocation();
        final var obj = operation.getObj();

        if (street != null && !street.isBlank()) {
            if (number != null && !number.isBlank()) {
                if (location != null && !location.isBlank()) {
                    if (obj != null && !obj.isBlank()) {
                        return Optional.of(String.format("%s, %s %s, %s", obj, street, number, location));
                    } else {
                        return Optional.of(String.format("%s %s, %s", street, number, location));
                    }
                } else if (obj != null && !obj.isBlank()) {
                    return Optional.of(String.format("%s, %s %s", obj, street, number));
                } else {
                    return Optional.of(String.format("%s %s", street, number));
                }
            } else if (location != null && !location.isBlank()) {
                if (obj != null && !obj.isBlank()) {
                    return Optional.of(String.format("%s, %s, %s", obj, street, location));
                } else {
                    return Optional.of(String.format("%s, %s", street, location));
                }
            } else if (obj != null && !obj.isBlank()) {
                return Optional.of(String.format("%s, %s", obj, street));
            } else {
                return Optional.of(street);
            }
        } else if (number != null && !number.isBlank()) {
            if (location != null && !location.isBlank()) {
                if (obj != null && !obj.isBlank()) {
                    return Optional.of(String.format("%s, %s, %s", obj, number, location));
                } else {
                    return Optional.of(String.format("%s, %s", number, location));
                }
            } else if (obj != null && !obj.isBlank()) {
                return Optional.of(String.format("%s, %s", obj, number));
            } else {
                return Optional.of(number);
            }
        } else if (location != null && !location.isBlank()) {
            if (obj != null && !obj.isBlank()) {
                return Optional.of(String.format("%s, %s", obj, location));
            } else {
                return Optional.of(location);
            }
        } else if (obj != null && !obj.isBlank()) {
            return Optional.of(obj);
        }

        return Optional.empty();
    }

    public String getGoogleMapsLink(OperationDto operation) {
        return getGoogleMapsLink(operation, null);
    }

    public String getGoogleMapsLink(OperationDto operation, String origin) {
        final var link = String.format(
                "https://www.google.com/maps/dir/?api=1&dir_action=navigate&travelmode=driving&destination=%s",
                encode(operation.getStreet() + " " + operation.getNumber() + ", " + operation.getLocation(),
                        StandardCharsets.UTF_8));
        return Optional.ofNullable(origin)
                .map(s -> link + "&origin=" + encode(s, StandardCharsets.UTF_8))
                .orElse(link);
    }
}
