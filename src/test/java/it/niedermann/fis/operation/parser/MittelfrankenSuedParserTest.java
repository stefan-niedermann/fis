package it.niedermann.fis.operation.parser;

import it.niedermann.fis.operation.TestUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SuppressWarnings("SpellCheckingInspection")
public class MittelfrankenSuedParserTest {

    private OperationParser parser;

    @BeforeEach
    public void setup() {
        this.parser = new MittelfrankenSuedParser();
    }

    @Test
    public void parseOperationFaxTest() throws IOException {
        final var samples = TestUtil.getOperationSamples("mittelfranken-sued");
        samples.entrySet().forEach(sample -> {
            final var expected = sample.getValue().expected();
            final var input = sample.getValue().input();

            final var stopWatch = new StopWatch();

            stopWatch.start();
            final var sampleDto = parser.parse(input);
            stopWatch.stop();

            assertTrue(stopWatch.getTotalTimeSeconds() <= 1,
                    "Sample " + sample.getKey() + " took more than one second to parse.");
            assertEquals("Failed to parse sample " + sample.getKey(), expected, sampleDto);
        });
    }

    @Test
    public void parseNoOperationFaxTest() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(null));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(""));
        assertThrows(IllegalArgumentException.class,
                () -> parser.parse("This is any other fax but has nothing to do with an operation."));
    }
}
