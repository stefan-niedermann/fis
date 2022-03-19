package it.niedermann.fis.operation.remote.notification;

import it.niedermann.fis.main.model.OperationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OperationNotificationUtilTest {

    private OperationNotificationUtil util;

    @BeforeEach()
    public void setup() {
        util = new OperationNotificationUtil();
    }

    @Test
    public void shouldBuildGoogleMapsLinks() {
        final var operation = new OperationDto();
        operation.setStreet("Foo Street");
        operation.setNumber("6b");
        operation.setLocation("Bar Village");
        assertEquals(
                "https://www.google.com/maps/dir/?api=1&dir_action=navigate&travelmode=driving&destination=Foo+Street+6b%2C+Bar+Village",
                util.getGoogleMapsLink(operation)
        );
    }

    @Test
    public void shouldBuildGoogleMapsLinksWithOrigin() {
        final var operation = new OperationDto();
        operation.setStreet("Foo Street");
        operation.setNumber("6b");
        operation.setLocation("Bar Village");
        assertEquals(
                "https://www.google.com/maps/dir/?api=1&dir_action=navigate&travelmode=driving&destination=Foo+Street+6b%2C+Bar+Village&origin=Qux+Place",
                util.getGoogleMapsLink(operation, "Qux Place")
        );
    }
}
