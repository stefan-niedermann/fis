package it.niedermann.fis.operation.parser;

import it.niedermann.fis.main.model.OperationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("SpellCheckingInspection")
class MittelfrankenSuedParser implements OperationParser {

    private static final Logger logger = LoggerFactory.getLogger(MittelfrankenSuedParser.class);

    /**
     * A list of all operation keywords, uppercase and sorted by length.
     *
     * @see <a href="https://www.stmi.bayern.de/assets/stmi/sus/rettungswesen/id3_26e_03_voe_03_fachthema_abek_in_by_einsatzstichwoerter_anl_2-1-4_20170307.pdf">stmi.bayern.de</a>
     */
    private static final Collection<String> OP_KEYWORDS = Set.of("ABC GEFAHRSTOFFMELDEANLAGE", "INF ÖFFENTLICHKEITSARBEIT", "EINSATZLEITUNG FEUERWEHR", "SON HILFE / SONSTIGES FW", "THL HUBSCHRAUBERLANDUNG", "THL VU SCHIFF KOLLISION", "SON HUBSCHRAUBERLANDUNG", "INF APOTHEKENAUSKUNFT", "INF HOCHWASSERMELDUNG", "INF VERKEHRSSICHERUNG", "INF ZAHNARZTNOTDIENST", "MED. TASK FORCE (MTF)", "THL GROSSTIERRETTUNG", "THL P EINGESCHLOSSEN", "ABC THL BIO / CHEMIE", "RD HILFE / SONSTIGES", "SON THW BEREITSCHAFT", "INF KASSENÄRZTLICHER", "INF SICHERHEITSWACHE", "GERÄTESATZ WALDBRAND", "THL FIRST RESPONDER", "THL GEBÄUDEEINSTURZ", "THL P RETTUNG H / T", "SON MOTORRADSTREIFE", "BEREITSCHAFTSDIENST", "INF LUFTBEOBACHTUNG", "INF UNWETTERWARNUNG", "DEKON-EINSATZKRÄFTE", "THL P STRASSENBAHN", "THL VU SCHIFF LECK", "ABC B BIO / CHEMIE", "EINSATZLEITUNG THW", "THL BOMBENDROHUNG", "THL P VERSCHÜTTET", "THL VU FLUGZEUG 1", "THL VU FLUGZEUG 2", "RD INFEKT GR4 / E", "SON ÜBERÖRTLICHER", "INF WACHBESETZUNG", "EINSATZLEITER BWB", "B SCHIENENTUNNEL", "B STRASSENTUNNEL", "THL RETTUNGSKORB", "RD MANV 51 – 100", "EINSATZLEITER RD", "EINSATZLEITER WR", "SCHWERG. PATIENT", "KATS-SONDERPLÄNE", "B ELEKTROANLAGE", "THL BELEUCHTUNG", "RD MANV 10 – 15", "RD MANV 16 – 25", "RD MANV 26 – 50", "SON BELEUCHTUNG", "INF ABNAHME BMA", "INF BMA STÖRUNG", "INF EIGENUNFALL", "SEG VERPFLEGUNG", "WASSERFÖRDERUNG", "WASSERTRANSPORT", "THL BOMBENFUND", "THL TRAGEHILFE", "ABC KRAFTSTOFF", "RD ABSICHERUNG", "RD BERGRETTUNG", "RD EISUNFALL 1", "RD EISUNFALL 2", "RD EISUNFALL 3", "RD MANV AB 100", "RD TAUCHUNFALL", "RD ÜBERÖRTLICH", "RD WASSERNOT 0", "RD WASSERNOT 1", "RD WASSERNOT 2", "RD WASSERNOT 3", "RD WASSERNOT 4", "RD WASSERNOT 5", "SON EINGLEISEN", "SON TRAGEHILFE", "INF GIFTNOTRUF", "INF PROBEALARM", "INF SAN-DIENST", "ERSTVERSORGUNG", "RETTUNGSZUG RD", "SEG BEHANDLUNG", "THL ERKUNDUNG", "ABC EXPLOSION", "ABC ÖL WASSER", "INF BMA PROBE", "SEG BETREUUNG", "SEG TRANSPORT", "THL P AUFZUG", "THL P U-BAHN", "THL UNWETTER", "ABC THL ATOM", "RD BETREUUNG", "RD KTP / RTW", "SON PSNV (B)", "SON PSNV (E)", "GEFAHRGUTZUG", "HUNDESTAFFEL", "THL AMOK FW", "THL P STROM", "THL SCHIENE", "ABC ÖL LAND", "RD SONSTIGE", "INF AUSFALL", "B 2 PERSON", "B 3 PERSON", "THL WASSER", "THL VU ZUG", "ABC B ATOM", "RD AMOK RD", "THL P ZUG", "RD 2-KIND", "SEG CBRNE", "B SCHIFF", "UG SANEL", "EINSATZ", "SEG IUK", "SEG T+S", "SEG THW", "B BOOT", "B WALD", "RD ITH", "RD ITW", "RD KTP", "RD VEF", "MESSEN", "UG ÖEL", "WARNEN", "B BMA", "B ZUG", "THL 1", "THL 2", "THL 3", "THL 4", "THL 5", "ABC 1", "ABC 2", "ABC 3", "ABC B", "SANEL", "RD 1", "RD 2", "RD 3", "RD 4", "RD 5", "B 1", "B 2", "B 3", "B 4", "B 5", "B 6", "B 7", "B 8", "ÖEL");
    /**
     * A list of possible terminating string literals, uppercase and sorted by most probable case.
     */
    private static final Collection<String> OP_FAX_END = Set.of("ALARMFAX ENDE", "ALARMFAX-ENDE", "ALARMFAX- ENDE", "RECHTLICHER HINWEIS");
    /**
     * A list of uppercase words which indicate that this is an actual operation fax.
     */
    private static final Collection<String> OP_FAX_REQUIRED_KEYWORDS = Set.of("ILS MITTELFRANKEN SÜD", "ALARMFAX", "EINSATZGRUND", "EINSATZMITTEL", "ILS MF-S");
    /**
     * A list of common OCR mistakes which should be purged from the input.
     */
    private static final Collection<Character> UNDESIRED_CHARACTERS = Set.of(',', ';', '.', ':', ' ', '`', '´', '\'', '"', '_', '-', '+', '*', '|', '»', '«');

    @Override
    public OperationDto parse(String input) throws IllegalArgumentException {
        return parseFax(input);
    }

    private static OperationDto parseFax(String input) throws IllegalArgumentException {
        if (!isOperationFax(input)) {
            throw new IllegalArgumentException("The input seems not to be an operation fax.");
        }

        final var dto = new OperationDto();
        final var lines = input.split("\n");

        runSafe("keyword", () -> dto.setKeyword(findKeyword(lines)));
        runSafe("tags", () -> dto.setTags(Arrays.asList(findTags(lines))));
        runSafe("street", () -> dto.setStreet(findValue("Straße", lines)));
        runSafe("number", () -> dto.setNumber(findValue("Haus-Nr.", lines)));
        runSafe("location", () -> dto.setLocation(findLocation(lines)));
        runSafe("object", () -> dto.setObj(findObject(lines)));
        runSafe("vehicles", () -> dto.setVehicles(Arrays.asList(findVehicles(lines))));
        runSafe("note", () -> dto.setNote(findNote(lines)));

        return dto;
    }

    private static String findObject(String[] lines) {
        var object = findValue("Objekt", lines);
        return object.length() > 2 ? object : "";
    }

    /**
     * We should not stop trying to get the extract the content of the operation if one part of the extraction fails unexpectedly
     *
     * @param subject  just a hint in case an exception occurs about where to look
     * @param runnable the actual extraction
     */
    private static void runSafe(String subject, Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            logger.warn("Error while trying to find " + subject, e);
        }
    }

    private static boolean isOperationFax(String input) {
        if (ObjectUtils.isEmpty(input)) {
            return false;
        }
        final var upper = input.toUpperCase(Locale.ROOT);
        return OP_FAX_REQUIRED_KEYWORDS
                .stream()
                .anyMatch(upper::contains);
    }

    private static String findNote(String[] lines) {
        final var notes = new LinkedList<String>();
        final var term = "BEMERKUNG";
        int cursor = 0;
        while (cursor < lines.length && !lines[cursor].toUpperCase(Locale.ROOT).contains(term)) {
            cursor++;
        }
        while (cursor + 1 < lines.length && !lineContainsEnd(lines[cursor + 1])) {
            cursor++;
            notes.add(trimSpecialCharacters(lines[cursor]));
        }
        return trimSpecialCharacters(String.join("\n", notes));
    }

    private static boolean lineContainsEnd(String line) {
        final var upper = line.toUpperCase(Locale.ROOT);
        return OP_FAX_END.stream().anyMatch(upper::contains);
    }

    private static String findKeyword(String[] lines) {
        final var value = findValue("Stichwort", lines);
        // In case the found value contains any more unwanted characters
        final var upperValue = value.toUpperCase(Locale.ROOT);
        for (var knownKeyword : OP_KEYWORDS) {
            if (upperValue.contains(knownKeyword)) {
                return knownKeyword;
            }
        }
        // When we didn't find anything by searching with findValue…
        if (trimSpecialCharacters(value).isEmpty()) {
            for (var line : lines) {
                for (var knownKeyword : OP_KEYWORDS) {
                    if (line.toUpperCase(Locale.ROOT).contains(knownKeyword)) {
                        return knownKeyword;
                    }
                }
            }
        }
        return value;
    }

    private static String[] findTags(String[] lines) {
        return Arrays.stream(findValue("Schlagw.", lines).split("#"))
                .map(MittelfrankenSuedParser::trimSpecialCharacters)
                .filter(potentialTag -> !potentialTag.isEmpty())
                .toArray(String[]::new);
    }

    private static String findLocation(String[] lines) {
        var location = findValue("Ort", lines);
        return String.join(" ", new LinkedHashSet<>(Arrays.asList(location.split(" "))));
    }

    private static String[] findVehicles(String[] lines) {
        final var term = "NAME";
        final var hits = new LinkedList<String>();
        for (var line : lines) {
            final var cleanedLine = trimSpecialCharacters(line);
            if (cleanedLine.toUpperCase(Locale.ROOT).startsWith(term)) {
                final var value = cleanedLine.trim().substring(term.length());
                hits.add(value);
            }
        }
        return hits.stream()
                .map(vehicle -> vehicle.replace("Florian", ""))
                .map(MittelfrankenSuedParser::trimSpecialCharacters)
                .map(vehicle -> vehicle.replace("änd", "and"))
                .map(vehicle -> vehicle.replace(".RH", "RH"))
                .map(vehicle -> vehicle.replaceAll("\\s+", " "))
                .filter(potentialVehicle -> !potentialVehicle.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .toArray(String[]::new);
    }

    private static String findValue(String term, String[] lines) {
        final var upperTerm = term.toUpperCase(Locale.ROOT);
        for (var line : lines) {
            final var cleanedLine = trimSpecialCharacters(line);
            if (cleanedLine.toUpperCase(Locale.ROOT).startsWith(upperTerm)) {
                final var value = cleanedLine.substring(term.length());
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
        return value.trim();
    }
}
