package it.niedermann.fis.operation;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.parser.OperationParserRepository;
import it.niedermann.fis.operation.remote.OperationRemoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import static it.niedermann.fis.operation.OperationTestUtil.createFTPFile;
import static java.time.Instant.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.*;

public class OperationApiDelegateImplTest {

    private OperationApiDelegateImpl api;
    private FisConfiguration config;
    private OperationRemoteRepository operationRemoteRepository;
    private OperationParserRepository operationParserRepository;

    @BeforeEach
    public void setup() {
        config = mock(FisConfiguration.class);
        when(config.getFtp()).thenReturn(mock(FisConfiguration.FtpConfiguration.class));
        when(config.getOperation()).thenReturn(mock(FisConfiguration.OperationConfiguration.class));
        when(config.getOperation().getDuration()).thenReturn(500L);
        operationRemoteRepository = mock(OperationRemoteRepository.class);
        operationParserRepository = mock(OperationParserRepository.class);
        this.api = new OperationApiDelegateImpl(
                config,
                operationRemoteRepository,
                operationParserRepository
        );
    }

    @Test
    public void shouldNotHaveAnyActiveOperationsBeforePolling() {
        final var resp = api.getOperation("");
        assertEquals("Asserting no active operation by default", HttpStatus.NO_CONTENT, resp.getStatusCode());
        assertNull("Asserting no active operation by default", resp.getBody());
    }

    @Test
    public void shouldNotHaveAnyActiveOperation_whenNoFTPFileIsGiven() {
        when(operationRemoteRepository.poll()).thenReturn(Optional.empty());

        api.pollOperations();

        final var resp = api.getOperation("");
        assertEquals("Should have no active operation when no FTP file is present", HttpStatus.NO_CONTENT, resp.getStatusCode());
        assertNull("Should have no active operation when no FTP file is present", resp.getBody());
    }

    @Test
    public void shouldNotHaveAnyActiveOperation_whenFTPDownloadFails() {
        when(operationRemoteRepository.poll()).thenReturn(Optional.of(createFTPFile("Foo.pdf", now())));
        when(operationRemoteRepository.download(any())).thenReturn(Optional.empty());

        api.pollOperations();

        final var resp = api.getOperation("");
        assertEquals("Should have no active operation when FTP download fails", HttpStatus.NO_CONTENT, resp.getStatusCode());
        assertNull("Should have no active operation when FTP download fails", resp.getBody());
    }

    @Test
    public void shouldNotHaveAnyActiveOperation_whenParsingFails() {
        when(operationRemoteRepository.poll()).thenReturn(Optional.of(createFTPFile("Foo.pdf", now())));
        when(operationRemoteRepository.download(any())).thenReturn(Optional.of(mock(File.class)));
        when(operationParserRepository.parse(any())).thenReturn(Optional.empty());

        api.pollOperations();

        final var resp = api.getOperation("");
        assertEquals("Should have no active operation when parsing fails", HttpStatus.NO_CONTENT, resp.getStatusCode());
        assertNull("Should have no active operation when parsing fails", resp.getBody());
    }

    @Test
    public void shouldReturnAnActiveOperation_whenAvailable() {
        when(operationRemoteRepository.poll()).thenReturn(Optional.of(createFTPFile("Foo.pdf", now())));
        when(operationRemoteRepository.download(any())).thenReturn(Optional.of(mock(File.class)));
        when(operationParserRepository.parse(any())).thenReturn(Optional.of(mock(OperationDto.class)));

        api.pollOperations();

        final var operation = api.getOperation("");
        assertEquals("Should return an active operation when available", HttpStatus.OK, operation.getStatusCode());
        assertNotNull("Should return an active operation when available", operation.getBody());
    }

    @Test
    public void shouldResetActiveOperations_afterGivenTime() throws InterruptedException {
        when(operationRemoteRepository.poll()).thenReturn(Optional.of(createFTPFile("Foo.pdf", now())));
        when(operationRemoteRepository.download(any())).thenReturn(Optional.of(mock(File.class)));
        when(operationParserRepository.parse(any())).thenReturn(Optional.of(mock(OperationDto.class)));

        api.pollOperations();

        final var resp1 = api.getOperation("");
        assertEquals("Should return an active operation when available", HttpStatus.OK, resp1.getStatusCode());
        assertNotNull("Should return an active operation when available", resp1.getBody());

        Thread.sleep(config.getOperation().getDuration() + 500L);

        final var resp2 = api.getOperation("");
        assertEquals("Should return an active operation when available", HttpStatus.NO_CONTENT, resp2.getStatusCode());
        assertNull("Should return an active operation when available", resp2.getBody());
    }

    @Test
    public void shouldDistributeNewActiveOperations_whileHavingAnotherActiveOperation() {
        final var operation1 = mock(OperationDto.class);
        when(operation1.getKeyword()).thenReturn("Foo");

        final var operation2 = mock(OperationDto.class);
        when(operation2.getKeyword()).thenReturn("Bar");

        when(operationRemoteRepository.poll()).thenReturn(Optional.of(createFTPFile("Foo.pdf", now())));
        when(operationRemoteRepository.download(any())).thenReturn(Optional.of(mock(File.class)));
        when(operationParserRepository.parse(any())).thenReturn(Optional.of(operation1));

        api.pollOperations();

        final var resp1 = api.getOperation("");
        assertEquals("Should return an active operation when available", HttpStatus.OK, resp1.getStatusCode());
        assertEquals("Should return an active operation when available", "Foo", Objects.requireNonNull(resp1.getBody()).getKeyword());

        when(operationParserRepository.parse(any())).thenReturn(Optional.of(operation2));

        api.pollOperations();

        final var resp2 = api.getOperation("");
        assertEquals("Should return an active operation when available", HttpStatus.OK, resp2.getStatusCode());
        assertEquals("Should return an active operation when available", "Bar", Objects.requireNonNull(resp2.getBody()).getKeyword());
    }

    /**
     * The first poll will be ignored and not published
     */
    private void pollFirstTime() {
        when(operationRemoteRepository.poll()).thenReturn(Optional.of(createFTPFile("Already existing PDF file.pdf", now())));

        api.pollOperations();
    }
}
