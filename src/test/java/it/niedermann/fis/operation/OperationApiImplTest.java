package it.niedermann.fis.operation;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.parser.OperationParserRepository;
import it.niedermann.fis.operation.remote.ftp.OperationFTPRepository;
import it.niedermann.fis.operation.remote.notification.OperationNotificationRepository;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import static it.niedermann.fis.operation.OperationTestUtil.createFTPFile;
import static java.time.Instant.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;

public class OperationApiImplTest {

    private OperationApiImpl api;
    private FisConfiguration config;
    private OperationFTPRepository operationFTPRepository;
    private OperationNotificationRepository operationNotificationRepository;
    private OperationParserRepository operationParserRepository;

    @BeforeEach
    public void setup() {
        config = mock(FisConfiguration.class);
        when(config.ftp()).thenReturn(mock(FisConfiguration.FtpConfiguration.class));
        when(config.operation()).thenReturn(mock(FisConfiguration.OperationConfiguration.class));
        when(config.operation().duration()).thenReturn(500L);
        operationFTPRepository = mock(OperationFTPRepository.class);
        operationNotificationRepository = mock(OperationNotificationRepository.class);
        operationParserRepository = mock(OperationParserRepository.class);
        this.api = new OperationApiImpl(
                config,
                operationFTPRepository,
                operationNotificationRepository,
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
        when(operationFTPRepository.poll()).thenReturn(Optional.empty());

        api.pollOperations();

        final var resp = api.getOperation("");
        assertEquals("Should have no active operation when no FTP file is present", HttpStatus.NO_CONTENT, resp.getStatusCode());
        assertNull("Should have no active operation when no FTP file is present", resp.getBody());
    }

    @Test
    public void shouldNotHaveAnyActiveOperation_whenFTPDownloadFails() {
        when(operationFTPRepository.poll()).thenReturn(Optional.of(createFTPFile("Foo.pdf", now())));
        when(operationFTPRepository.download(any())).thenReturn(Optional.empty());

        api.pollOperations();

        final var resp = api.getOperation("");
        assertEquals("Should have no active operation when FTP download fails", HttpStatus.NO_CONTENT, resp.getStatusCode());
        assertNull("Should have no active operation when FTP download fails", resp.getBody());
    }

    @Test
    public void shouldNotHaveAnyActiveOperation_whenParsingFails() {
        when(operationFTPRepository.poll()).thenReturn(Optional.of(createFTPFile("Foo.pdf", now())));
        when(operationFTPRepository.download(any())).thenReturn(Optional.of(mock(File.class)));
        when(operationParserRepository.parse(any())).thenReturn(Optional.empty());

        api.pollOperations();

        final var resp = api.getOperation("");
        assertEquals("Should have no active operation when parsing fails", HttpStatus.NO_CONTENT, resp.getStatusCode());
        assertNull("Should have no active operation when parsing fails", resp.getBody());
    }

    @Test
    public void shouldReturnAnActiveOperation_whenAvailable() {
        when(operationFTPRepository.poll()).thenReturn(Optional.of(createFTPFile("Foo.pdf", now())));
        when(operationFTPRepository.awaitUploadCompletion(any())).thenReturn(Optional.of(mock(FTPFile.class)));
        when(operationFTPRepository.download(any())).thenReturn(Optional.of(mock(File.class)));
        when(operationParserRepository.parse(any())).thenReturn(Optional.of(mock(OperationDto.class)));

        api.pollOperations();

        final var operation = api.getOperation("");
        assertEquals("Should return an active operation when available", HttpStatus.OK, operation.getStatusCode());
        assertNotNull("Should return an active operation when available", operation.getBody());
    }

    @Test
    public void shouldSendAMail_whenOperationAvailable() {
        when(operationFTPRepository.poll()).thenReturn(Optional.of(createFTPFile("Foo.pdf", now())));
        when(operationFTPRepository.awaitUploadCompletion(any())).thenReturn(Optional.of(mock(FTPFile.class)));
        when(operationFTPRepository.download(any())).thenReturn(Optional.of(mock(File.class)));
        when(operationParserRepository.parse(any())).thenReturn(Optional.of(mock(OperationDto.class)));

        api.pollOperations();

        verify(operationNotificationRepository, times(1)).accept(any(OperationDto.class));

        api.pollOperations();

        verify(operationNotificationRepository, times(2)).accept(any(OperationDto.class));
    }

    @Test
    public void shouldResetActiveOperations_afterGivenTime() throws InterruptedException {
        when(operationFTPRepository.poll()).thenReturn(Optional.of(createFTPFile("Foo.pdf", now())));
        when(operationFTPRepository.awaitUploadCompletion(any())).thenReturn(Optional.of(mock(FTPFile.class)));
        when(operationFTPRepository.download(any())).thenReturn(Optional.of(mock(File.class)));
        when(operationParserRepository.parse(any())).thenReturn(Optional.of(mock(OperationDto.class)));

        api.pollOperations();

        final var resp1 = api.getOperation("");
        assertEquals("Should return an active operation when available", HttpStatus.OK, resp1.getStatusCode());
        assertNotNull("Should return an active operation when available", resp1.getBody());

        Thread.sleep(config.operation().duration() + 500L);

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

        when(operationFTPRepository.poll()).thenReturn(Optional.of(createFTPFile("Foo.pdf", now())));
        when(operationFTPRepository.awaitUploadCompletion(any())).thenReturn(Optional.of(mock(FTPFile.class)));
        when(operationFTPRepository.download(any())).thenReturn(Optional.of(mock(File.class)));
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
        when(operationFTPRepository.poll()).thenReturn(Optional.of(createFTPFile("Already existing PDF file.pdf", now())));

        api.pollOperations();
    }
}
