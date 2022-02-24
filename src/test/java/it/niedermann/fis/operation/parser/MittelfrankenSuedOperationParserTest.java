package it.niedermann.fis.operation.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.niedermann.fis.main.model.OperationDto;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StopWatch;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SuppressWarnings("SpellCheckingInspection")
public class MittelfrankenSuedOperationParserTest {

    private OperationParser parser;

    @BeforeEach
    public void setup() {
        this.parser = new MittelfrankenSuedParser();
    }

    @Test
    public void parseOperationFaxTest() throws IOException {
        // TODO find samples dynamically
        final var samples = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        for (var sample : samples) {
            final var expected = getSampleExpected(sample);
            final var input = getSampleInput(sample);

            final var stopWatch = new StopWatch();

            stopWatch.start();
            final var sampleDto = parser.parse(input);
            stopWatch.stop();

            assertTrue(stopWatch.getTotalTimeSeconds() <= 1, "Sample " + sample + " took more than one second to parse.");
            assertEquals("Failed to parse sample " + sample, expected, sampleDto);
        }
    }

    @Test
    public void parseNoOperationFaxTest() {
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
