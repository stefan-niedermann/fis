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

public class MittelfrankenSuedParserTest {

    @Test
    public void parseTest() throws IOException {
        final int[] samples = new int[] {1};
        final OperationFaxParser parser = OperationFaxParser.create("mittelfranken-sued");
        for(int sample : samples) {
            final OperationDto sampleDto = parser.parse(getSampleInput(sample));
            final OperationDto expectedDto = getSampleExpected(sample);
            assertEquals(expectedDto, sampleDto);
        }
    }

    private String getSampleInput(@SuppressWarnings("SameParameterValue") int number) throws IOException {
        return IOUtils.toString(new ClassPathResource("samples/mittelfranken-sued/" + number + "-sample.txt").getInputStream(), UTF_8);
    }

    private OperationDto getSampleExpected(@SuppressWarnings("SameParameterValue") int number) throws IOException {
        return new ObjectMapper().readValue(new ClassPathResource("samples/mittelfranken-sued/" + number + "-expected.json").getInputStream(), OperationDto.class);
    }
}
