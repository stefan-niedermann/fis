package it.niedermann.fis.operation.parser;

import it.niedermann.fis.operation.OperationDto;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

class MittelfrankenSuedParser implements OperationFaxParser {

    private final static Collection<String> OP_FAX_REQUIRED_KEYWORDS = Arrays.asList(
            "ILS MITTELFRANKEN SÜD", "ALARMFAX", "EINSATZGRUND", "EINSATZMITTEL", "ILS MF-S"
    );
    private final static Collection<Character> UNDESIRED_CHARACTERS = Arrays.asList(',', ';', '.', ':', ' ', '`', '´', '\'', '"', '_', '-', '+', '*');

    @Override
    public OperationDto parse(String input) throws IllegalArgumentException {
        return parseFax(input);
    }

    private static OperationDto parseFax(String input) throws IllegalArgumentException {
        if(!isOperationFax(input)) {
            throw new IllegalArgumentException("The input seems not to be an operation fax.");
        }
        
        final var dto = new OperationDto();
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

    private static boolean isOperationFax(String input) {
        if(ObjectUtils.isEmpty(input)) {
            return false;
        }
        final var upper = input.toUpperCase(Locale.ROOT);
        return OP_FAX_REQUIRED_KEYWORDS
                .stream()
                .anyMatch(upper::contains);
    }

    private static String findNote(String[] lines) {
        final Collection<String> notes = new LinkedList<>();
        int cursor = 0;
        while (cursor < lines.length && !lines[cursor].contains("BEMERKUNG")) {
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
        while (value.length() > 0 && UNDESIRED_CHARACTERS.contains(value.charAt(0))) {
            value = value.substring(1).stripLeading();
        }
        while (value.length() > 0 && UNDESIRED_CHARACTERS.contains(value.charAt(value.length() - 1))) {
            value = value.substring(0, value.length() - 1).stripTrailing();
        }
        return value;
    }
}
