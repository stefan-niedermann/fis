package it.niedermann.fis;

import it.niedermann.fis.operation.OperationInformationDto;
import it.niedermann.fis.operation.parser.OperationFaxParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MittelfrankenSuedParserTest {

    final static String ocr = "12/12/2012 13:13 +4998 987654321 FF MUSTERWEHR 5, 81/81\n" +
            "BF en ES Taten EEE ET\n" +
            "Sraaunn FAX sunnun FAX annmn- FAX =r--7u FAX uumun FAX mrz7u0 FAX enmmnmm\n" +
            ". Absender : ILS MITTELFRANKEN SÜD ; ;\n" +
            "* Fax : +49 (9876) / 5432101\n" +
            "' Termin z ' )\n" +
            "Einsatznummer: B 5.3 201024 630 ; ; ' ;.\n" +
            "rk anumum  MITTEILER -4=nnnuheunenrer rennen\n" +
            "anna akkamn an amr Eu rumnm EINSATZORT -+u-=nuuur en nenn Eee emnnn em nne\n" +
            "; Straße : Musterstraße ;\n" +
            "Haus-Nr. :\n" +
            "ort : 99999 Musterdorf - Mustergemeinde Mustergemeinde -\n" +
            "Objekt Ce | |\n" +
            "Station : 0 ; ;\n" +
            "‚ X=1234567.89\n" +
            "Y=9876543. 21 ; ; ; ; ;\n" +
            "A\n" +
            "* straße : Haus-Nr. : ;\n" +
            "Ort a '\n" +
            "Objekt\n" +
            "Station\n" +
            "ELLE au ku nun aa mn nEEEEEEn PATIENT ----mrneeenmmn nennen memnn nenne\n" +
            "Bm EINSATZGRUND ==mnne men\n" +
            "Schlagw.: #B1014#im Freien#Abfall-, Müll-, Papiercontainer _\n" +
            "Stichwort: B 1 ' '\n" +
            "A uumamum EINSATZMITTEL «nn nn m\n" +
            "; Name : 9.8.7 RH FF Musterwehr ;\n" +
            "; Alarmiert. ; 12.12.2012 14:10:15\n" +
            "Aus : ; ; ; ;\n" +
            "Name : Florian Musterwehr 24/1\n" +
            "Alarmiert : 12.12.2012 14:10:15\n" +
            "Aus : ;\n" +
            "Name : Florian Roth Land 7/8 ; .\n" +
            "; Alarmiert : 12.12.2012 14:10:15\n" +
            "Aus : ; .\n" +
            "; Name : Florian Roth Länd 9/5 ; ;\n" +
            "Alarmiert : 12.12.2012 14:16:15\n" +
            "Aus ;\n" +
            "Name ;. Florian Mustergemeinde 14/5 ;\n" +
            "Alarmiert : 12.12.2012 14:10:16\n" +
            "Aus BL ; ;\n" +
            "na nuanun BEMERKUNG -=---- ru reknnamnmn Eee een nue)\n" +
            "Container qualmt leicht - vmtl. heiße Asche (sichtbar) .\n" +
            "im Gelände ehem. Brennerei ,\n" +
            "een ALARMFAX ENDE Akememaneneenmeneeeee nennen\n" +
            "Kb .";

    @Test
    public void parseTest() {
        final OperationFaxParser parser = OperationFaxParser.create("mittelfranken-sued");
        final OperationInformationDto dto = parser.parse(ocr);

        assertEquals("", dto.number);

        assertEquals("Musterstraße", dto.street);

        assertEquals(3, dto.tags.length);
        assertEquals("B1014", dto.tags[0]);
        assertEquals("im Freien", dto.tags[1]);
        assertEquals("Abfall-, Müll-, Papiercontainer", dto.tags[2]);

        assertEquals("99999 Musterdorf - Mustergemeinde", dto.location);

        assertEquals(5, dto.vehicles.length);
        assertEquals("9.8.7 RH FF Musterwehr", dto.vehicles[0]);
        assertEquals("Musterwehr 24/1", dto.vehicles[1]);
        assertEquals("Roth Land 7/8", dto.vehicles[2]);
        assertEquals("Roth Land 9/5", dto.vehicles[3]);
        assertEquals("Mustergemeinde 14/5", dto.vehicles[4]);

        assertEquals("Container qualmt leicht - vmtl. heiße Asche (sichtbar)\n" +
                        "im Gelände ehem. Brennerei", dto.note);
    }
}
