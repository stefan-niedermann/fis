package it.niedermann.fis.operation.parser;

import it.niedermann.fis.operation.OperationInformationDto;

import java.util.*;
import java.util.stream.Collectors;

class MittelfrankenSuedParser implements OperationFaxParser {

    final static List<Character> characters = Arrays.asList(',', ';', '.', ':', ' ', '`', '´', '\'', '"', '_', '-', '+', '*');

    @Override
    public OperationInformationDto parse(String input) {
        return parseFax(input);
    }

    private static OperationInformationDto parseFax(String input) {
        final var dto = new OperationInformationDto();
        if (input == null || input.isEmpty()) {
            return dto;
        }

        final var lines = input.split("\n");

        dto.keyword = findValue("Stichwort", lines);
        dto.tags = findTags(lines);
        dto.street = findStreet(lines);
        dto.number = findValue("Haus-Nr.", lines);
        dto.location = findLocation(lines);
        dto.vehicles = findVehicles(lines);
        dto.note = findNote(lines);

        return dto;
    }

    private static String findNote(String[] lines) {
        final Collection<String> notes = new LinkedList<>();
        int cursor = 0;
        while (cursor <= lines.length && !lines[cursor].contains("BEMERKUNG")) {
            cursor++;
        }
        while (cursor < lines.length && !lines[cursor + 1].contains("ALARMFAX ENDE")) {
            cursor++;
            notes.add(trimSpecialCharacters(lines[cursor]));
        }
        return String.join("\n", notes);
    }

    private static String[] findTags(String[] lines) {
        return Arrays.stream(findValue("Schlagw.", lines).split("#"))
                .map(MittelfrankenSuedParser::trimSpecialCharacters)
                .filter(potentialTag -> !potentialTag.isEmpty())
                .toArray(String[]::new);
    }

    private static String findStreet(String[] lines) {
        var street = findValue("Straße", lines);
        if (street.isEmpty()) {
            street = findValue("straße", lines);
        }
        return street;
    }

    private static String findLocation(String[] lines) {
        var location = findValue("ort", lines);
        if (location.isEmpty()) {
            location = findValue("Ort", lines);
        }
        return String.join(" ", new LinkedHashSet<>(Arrays.asList(location.split(" "))));
    }

    private static String[] findVehicles(String[] lines) {
        final var term = "Name";
        final var hits = new ArrayList<String>();
        for (var line : lines) {
            final var cleanedLine = trimSpecialCharacters(line);
            if (cleanedLine.startsWith(term)) {
                final var value = cleanedLine.trim().substring(term.length());
                hits.add(value);
            }
        }
        return hits.stream()
                .map(vehicle -> vehicle.replace("Florian", ""))
                .map(MittelfrankenSuedParser::trimSpecialCharacters)
                .map(vehicle -> vehicle.replace("Länd", "Land"))
                .filter(potentialVehicle -> !potentialVehicle.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .toArray(String[]::new);
    }

    private static String findValue(String term, String[] lines) {
        for (var line : lines) {
            final var cleanedLine = trimSpecialCharacters(line);
            if (cleanedLine.startsWith(term)) {
                final var value = cleanedLine.trim().substring(term.length());
                return trimSpecialCharacters(value);
            }
        }
        return "";
    }

    private static String trimSpecialCharacters(String value) {
        while (value.length() > 0 && characters.contains(value.charAt(0))) {
            value = value.substring(1).stripLeading();
        }
        while (value.length() > 0 && characters.contains(value.charAt(value.length() - 1))) {
            value = value.substring(0, value.length() - 1).stripTrailing();
        }
        return value;
    }
}
