package it.niedermann.fis.operation.remote.notification;

import it.niedermann.fis.main.model.OperationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                util.getGoogleMapsLink(operation));
    }

    @Test
    public void shouldBuildGoogleMapsLinksWithOrigin() {
        final var operation = new OperationDto();
        operation.setStreet("Foo Street");
        operation.setNumber("6b");
        operation.setLocation("Bar Village");
        assertEquals(
                "https://www.google.com/maps/dir/?api=1&dir_action=navigate&travelmode=driving&destination=Foo+Street+6b%2C+Bar+Village&origin=Qux+Place",
                util.getGoogleMapsLink(operation, "Qux Place"));
    }

    // Given no parameter

    @Test
    public void shouldHandleNoParameter() {
        final var operation = new OperationDto();
        assertTrue(util.getHumanReadableLocation(operation).isEmpty());
    }

    // Given one parameter

    @Test
    public void shouldHandleOnlyStreet() {
        final var operation = new OperationDto();
        operation.setStreet("Foo Street");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Foo Street", util.getHumanReadableLocation(operation).get());
    }

    @Test
    public void shouldHandleOnlyNumber() {
        final var operation = new OperationDto();
        operation.setNumber("16B");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("16B", util.getHumanReadableLocation(operation).get());
    }

    @Test
    public void shouldHandleOnlyLocation() {
        final var operation = new OperationDto();
        operation.setLocation("Bar Village");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Bar Village", util.getHumanReadableLocation(operation).get());
    }

    @Test
    public void shouldHandleOnlyObject() {
        final var operation = new OperationDto();
        operation.setObj("Church");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Church", util.getHumanReadableLocation(operation).get());
    }

    // Given two parameters

    @Test
    public void shouldHandleOnlyStreetAndNumber() {
        final var operation = new OperationDto();
        operation.setStreet("Foo Street");
        operation.setNumber("16B");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Foo Street 16B", util.getHumanReadableLocation(operation).get());
    }

    @Test
    public void shouldHandleOnlyStreetAndLocation() {
        final var operation = new OperationDto();
        operation.setStreet("Foo Street");
        operation.setLocation("Bar Village");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Foo Street, Bar Village", util.getHumanReadableLocation(operation).get());
    }

    @Test
    public void shouldHandleOnlyStreetAndObject() {
        final var operation = new OperationDto();
        operation.setStreet("Foo Street");
        operation.setObj("Church");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Church, Foo Street", util.getHumanReadableLocation(operation).get());
    }

    @Test
    public void shouldHandleOnlyNumberAndLocation() {
        final var operation = new OperationDto();
        operation.setNumber("16B");
        operation.setLocation("Bar Village");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("16B, Bar Village", util.getHumanReadableLocation(operation).get());
    }

    @Test
    public void shouldHandleOnlyNumberAndObject() {
        final var operation = new OperationDto();
        operation.setNumber("16B");
        operation.setObj("Church");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Church, 16B", util.getHumanReadableLocation(operation).get());
    }

    @Test
    public void shouldHandleOnlyLocationAndObject() {
        final var operation = new OperationDto();
        operation.setLocation("Bar Village");
        operation.setObj("Church");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Church, Bar Village", util.getHumanReadableLocation(operation).get());
    }

    // Given three parameters

    @Test
    public void shouldHandleOnlyStreetAndNumberAndLocation() {
        final var operation = new OperationDto();
        operation.setStreet("Foo Street");
        operation.setNumber("16B");
        operation.setLocation("Bar Village");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Foo Street 16B, Bar Village", util.getHumanReadableLocation(operation).get());
    }

    @Test
    public void shouldHandleOnlyStreetAndNumberAndObject() {
        final var operation = new OperationDto();
        operation.setStreet("Foo Street");
        operation.setNumber("16B");
        operation.setObj("Church");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Church, Foo Street 16B", util.getHumanReadableLocation(operation).get());
    }

    @Test
    public void shouldHandleOnlyStreetAndLocationAndObject() {
        final var operation = new OperationDto();
        operation.setStreet("Foo Street");
        operation.setLocation("Bar Village");
        operation.setObj("Church");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Church, Foo Street, Bar Village", util.getHumanReadableLocation(operation).get());
    }

    @Test
    public void shouldHandleOnlyNumberAndLocationAndObject() {
        final var operation = new OperationDto();
        operation.setNumber("16B");
        operation.setLocation("Bar Village");
        operation.setObj("Church");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Church, 16B, Bar Village", util.getHumanReadableLocation(operation).get());
    }

    @Test
    public void shouldHandleStreetAndNumberAndLocationAndObject() {
        final var operation = new OperationDto();
        operation.setStreet("Foo Street");
        operation.setNumber("16B");
        operation.setLocation("Bar Village");
        operation.setObj("Church");
        assertTrue(util.getHumanReadableLocation(operation).isPresent());
        assertEquals("Church, Foo Street 16B, Bar Village", util.getHumanReadableLocation(operation).get());
    }
}
