package it.niedermann.fis.operation.parser;

import it.niedermann.fis.main.model.OperationDto;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OperationParserRepositoryTest {

    private OperationParserRepository repository;
    private Tesseract tesseract;
    private OperationParser parser;

    @BeforeEach
    public void setup() {
        tesseract = mock(Tesseract.class);
        final var tesseractFactory = mock(OperationTesseractFactory.class);
        when(tesseractFactory.createTesseract(any())).thenReturn(tesseract);
        parser = mock(OperationParser.class);
        final var operationParserFactory = mock(OperationParserFactory.class);
        when(operationParserFactory.createParser(any())).thenReturn(parser);
        this.repository = new OperationParserRepository(
                mock(TesseractConfiguration.class),
                tesseractFactory,
                operationParserFactory
        );
    }

    @Test
    public void shouldReturnOperationDto() {
        when(parser.parse(any())).thenReturn(mock(OperationDto.class));
        assertTrue(repository.parse(new File("")).isPresent());
    }

    @Test
    public void shouldReturnEmptyWhenFileIsNoOperationFax() {
        when(parser.parse(any())).thenThrow(IllegalArgumentException.class);
        assertTrue(repository.parse(new File("")).isEmpty());
    }

    @Test
    public void shouldReturnEmptyWhenTesseractFails() throws TesseractException {
        when(tesseract.doOCR(any(File.class))).thenThrow(TesseractException.class);
        assertTrue(repository.parse(new File("")).isEmpty());
    }
}
