package it.niedermann.fis;

import it.niedermann.fis.operation.OperationInformationDto;
import it.niedermann.fis.operation.parser.OperationFaxParser;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MittelfrankenSuedParserTest {

    private String getSample(@SuppressWarnings("SameParameterValue") int number) throws IOException {
        return IOUtils.toString(new ClassPathResource("samples/mittelfranken-sued/" + number + ".txt").getInputStream(), UTF_8);
    }

    @Test
    public void parseTest() throws IOException {
        final OperationFaxParser parser = OperationFaxParser.create("mittelfranken-sued");
        final OperationInformationDto dto = parser.parse(getSample(1));

        assertEquals("", dto.number);

        assertEquals("Musterdstraße", dto.street);

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
