package it.niedermann.fis;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.niedermann.fis.operation.OperationDto;
import it.niedermann.fis.operation.parser.OperationFaxParser;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MittelfrankenSuedParserTest {

    @Test
    public void parseOperationFaxTest() throws IOException {
        final var samples = new int[] {1};
        final var parser = OperationFaxParser.create("mittelfranken-sued");
        for(var sample : samples) {
            final OperationDto sampleDto = parser.parse(getSampleInput(sample));
            final OperationDto expectedDto = getSampleExpected(sample);
            assertEquals(expectedDto, sampleDto);
        }
    }

    @Test
    public void parseNoOperationFaxTest() {
        final var parser = OperationFaxParser.create("mittelfranken-sued");
        assertThrows(IllegalArgumentException.class, () -> parser.parse(null));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(""));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("This is any other fax but has nothing to do with an operation."));
    }

    private String getSampleInput(@SuppressWarnings("SameParameterValue") int number) throws IOException {
        return IOUtils.toString(new ClassPathResource("samples/mittelfranken-sued/" + number + "-sample.txt").getInputStream(), UTF_8);
    }

    private OperationDto getSampleExpected(@SuppressWarnings("SameParameterValue") int number) throws IOException {
        return new ObjectMapper().readValue(new ClassPathResource("samples/mittelfranken-sued/" + number + "-expected.json").getInputStream(), OperationDto.class);
    }
}
