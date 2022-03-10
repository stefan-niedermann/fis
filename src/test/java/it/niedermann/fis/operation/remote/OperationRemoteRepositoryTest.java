package it.niedermann.fis.operation.remote;

import it.niedermann.fis.FisConfiguration;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

import static it.niedermann.fis.operation.OperationTestUtil.createFTPFile;
import static it.niedermann.fis.operation.OperationTestUtil.createFTPObject;
import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OperationRemoteRepositoryTest {

    private OperationRemoteRepository repository;
    private FTPClient ftpClient;
    private Instant beforeServerStart;

    @BeforeEach
    public void setup() throws IOException {
        beforeServerStart = now();
        final var ftpConfig = mock(FisConfiguration.FtpConfiguration.class);
        when(ftpConfig.fileSuffix()).thenReturn(".pdf");
        when(ftpConfig.checkUploadCompleteInterval()).thenReturn(0L);
        when(ftpConfig.checkUploadCompleteMaxAttempts()).thenReturn(10);
        when(ftpConfig.maxFileSize()).thenReturn(10_000_000L);
        final var config = mock(FisConfiguration.class);
        when(config.ftp()).thenReturn(ftpConfig);
        ftpClient = mock(FTPClient.class);
        when(ftpClient.login(any(), any())).thenReturn(true);
        final var ftpClientFactory = mock(OperationFTPClientFactory.class);
        when(ftpClientFactory.createFTPClient(config)).thenReturn(ftpClient);
        this.repository = new OperationRemoteRepository(
                config,
                ftpClientFactory
        );
    }

    @Test
    public void shouldSkipOnError() throws IOException {
        when(ftpClient.listFiles(any())).thenThrow(new IOException());
        assertTrue(repository.poll().isEmpty());
    }

    @Test
    public void shouldSkipOtherTypesThanFiles() {
        List.of(
                FTPFile.DIRECTORY_TYPE,
                FTPFile.UNKNOWN_TYPE,
                FTPFile.SYMBOLIC_LINK_TYPE
        ).stream()
                .map(type -> createFTPObject("Foo", now(), FTPFile.DIRECTORY_TYPE))
                .map(ftpObject -> new FTPFile[]{ftpObject})
                .forEach(fileList -> {
                    try {
                        when(ftpClient.listFiles(any())).thenReturn(fileList);
                        assertTrue(repository.poll().isEmpty());
                    } catch (IOException e) {
                        fail(e);
                    }
                });
    }

    @Test
    public void shouldSkipEmptyRemote() throws IOException {
        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[0]);
        assertTrue(repository.poll().isEmpty());
    }

    @Test
    public void shouldSkipExistingFilesButReturnNewFiles() throws IOException {
        final var existingFoo = createFTPFile("Foo.pdf", beforeServerStart);
        final var existingBar = createFTPFile("Bar.pdf", beforeServerStart);

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                existingFoo, existingBar
        });
        assertTrue(repository.poll().isEmpty());

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                existingFoo, createFTPFile("Qux.pdf", nextPolledInstant()), existingBar
        });
        final var addedFile = repository.poll();
        assertTrue(addedFile.isPresent());
        assertEquals("Qux.pdf", addedFile.get().getName());
    }

    @Test
    public void shouldSkipTheSameFileWhenPollAgain() throws IOException {
        final var response = new FTPFile[]{createFTPFile("Foo.pdf", nextPolledInstant())};
        when(ftpClient.listFiles(any())).thenReturn(response);

        assertTrue(repository.poll().isPresent());
        assertTrue(repository.poll().isEmpty());
    }

    @Test
    public void shouldFilterBySuffix() throws IOException {
        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Foo.doc", nextPolledInstant())
        });
        assertTrue(repository.poll().isEmpty());
    }

    @Test
    public void shouldReturnTheAlphabeticallyHigherFileInCaseOfEqualTimestamps() throws IOException {
        final var timestamp = nextPolledInstant();
        final var response = new FTPFile[]{
                createFTPFile("Bar.pdf", timestamp),
                createFTPFile("Foo.pdf", timestamp)
        };
        when(ftpClient.listFiles(any())).thenReturn(response);
        final var result = repository.poll();
        assertTrue(result.isPresent());
        assertEquals("Foo.pdf", result.get().getName());
    }

    @Test
    public void shouldSkipNewFilesWithSameOrLowerTimestampThanTheLastOne() throws IOException {
        final var timestamp = nextPolledInstant();

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Foo.pdf", timestamp),
        });
        final var file = repository.poll();
        assertTrue(file.isPresent());
        assertEquals("Foo.pdf", file.get().getName());

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Foo.pdf", timestamp),
                createFTPFile("Bar.pdf", timestamp.minus(5, ChronoUnit.MINUTES))
        });
        assertTrue(repository.poll().isEmpty());

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Bar.pdf", timestamp.minus(5, ChronoUnit.MINUTES)),
                createFTPFile("Foo.pdf", timestamp)
        });
        assertTrue(repository.poll().isEmpty());

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Bar.pdf", timestamp),
                createFTPFile("Foo.pdf", timestamp)
        });
        assertTrue(repository.poll().isEmpty());

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Bar.pdf", timestamp),
                createFTPFile("Foo.pdf", timestamp)
        });
        assertTrue(repository.poll().isEmpty());
    }

    @Test
    public void shouldReturnTheMostRecentFile() throws IOException {
        final var timestamp = nextPolledInstant();

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Foo.pdf", timestamp),
        });
        final var file1 = repository.poll();
        assertTrue(file1.isPresent());
        assertEquals("Foo.pdf", file1.get().getName());

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Foo.pdf", timestamp),
                createFTPFile("Bar.pdf", timestamp.plus(5, ChronoUnit.MINUTES))
        });
        final var file2 = repository.poll();
        assertTrue(file2.isPresent());
        assertEquals("Bar.pdf", file2.get().getName());

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Foo.pdf", timestamp),
                createFTPFile("Qux.pdf", timestamp.plus(10, ChronoUnit.MINUTES)),
                createFTPFile("Bar.pdf", timestamp.plus(5, ChronoUnit.MINUTES))
        });
        final var file3 = repository.poll();
        assertTrue(file3.isPresent());
        assertEquals("Qux.pdf", file3.get().getName());
    }

    @Test
    public void pollingShouldSkipHugeFiles() throws IOException {
        when(ftpClient.listFiles(any())).thenReturn(
                new FTPFile[]{createFTPFile("Foo.pdf", nextPolledInstant(), 10_000_001L)}
        );
        assertTrue(repository.poll().isEmpty());
    }

    @Test
    public void downloadShouldReturnEmptyWhenRetrievingFileThrowsIOException() throws IOException {
        when(ftpClient.retrieveFile(any(), any())).thenThrow(IOException.class);
        assertTrue(repository.download(new FTPFile()).isEmpty());
    }

    @Test
    public void downloadShouldReturnEmptyWhenRetrievingFileFails() throws IOException {
        when(ftpClient.retrieveFile(any(), any())).thenReturn(false);
        assertTrue(repository.download(new FTPFile()).isEmpty());
    }

    @Test
    public void downloadShouldReturnLocalFile() throws IOException {
        when(ftpClient.retrieveFile(any(), any())).thenReturn(true);
        assertTrue(repository.download(new FTPFile()).isPresent());
    }

    @Test
    public void uploadAlreadyCompleted() throws IOException {
        when(ftpClient.listFiles(any(), any())).thenReturn(
                new FTPFile[]{createFTPFile("Foo.pdf", now(), 444)}
        );
        final var file = new FTPFile();
        file.setName("Foo.pdf");
        file.setSize(444);
        assertTrue(repository.awaitUploadCompletion(file).isPresent());
    }

    @Test
    public void uploadAlreadyCompletedTotalSizeZero() throws IOException {
        when(ftpClient.listFiles(any(), any())).thenReturn(
                new FTPFile[]{createFTPFile("Foo.pdf", now(), 0)}
        );
        final var file = new FTPFile();
        file.setName("Foo.pdf");
        file.setSize(0);
        assertTrue(repository.awaitUploadCompletion(file).isPresent());
    }

    @Test
    public void uploadInProgress() throws IOException {
        when(ftpClient.listFiles(any(), any())).thenReturn(
                new FTPFile[]{createFTPFile("Foo.pdf", now(), 333)},
                new FTPFile[]{createFTPFile("Foo.pdf", now(), 444)},
                new FTPFile[]{createFTPFile("Foo.pdf", now(), 555)}
        );
        final var file = new FTPFile();
        file.setName("Foo.pdf");
        file.setSize(111);
        assertTrue(repository.awaitUploadCompletion(file).isPresent());
    }

    @Test
    public void uploadInProgressStartingWithZero() throws IOException {
        when(ftpClient.listFiles(any(), any())).thenReturn(
                new FTPFile[]{createFTPFile("Foo.pdf", now(), 333)},
                new FTPFile[]{createFTPFile("Foo.pdf", now(), 444)},
                new FTPFile[]{createFTPFile("Foo.pdf", now(), 555)}
        );
        final var file = new FTPFile();
        file.setName("Foo.pdf");
        file.setSize(0);
        assertTrue(repository.awaitUploadCompletion(file).isPresent());
    }

    @Test
    public void awaitUploadCompletionShouldStopAfterConfiguredAttempts() throws IOException {
        when(ftpClient.listFiles(any(), any())).thenReturn(
                new FTPFile[]{createFTPFile("Foo.pdf", now(), 2)},
                IntStream
                        .rangeClosed(3, 11) // Bigger than the configured count of attempts
                        .boxed()
                        .map(size -> new FTPFile[]{createFTPFile("Foo.pdf", now(), size)})
                        .toArray(FTPFile[][]::new)
        );
        final var file = new FTPFile();
        file.setName("Foo.pdf");
        file.setSize(1);
        assertTrue(repository.awaitUploadCompletion(file).isEmpty());
    }

    @Test
    public void awaitUploadCompletionShouldGracefullyHandleErrors() throws IOException {
        when(ftpClient.listFiles(any(), any())).thenThrow(new IOException());
        final var file = new FTPFile();
        file.setName("Foo.pdf");
        file.setSize(111);
        assertTrue(repository.awaitUploadCompletion(file).isEmpty());
    }

    @Test
    public void awaitUploadCompletionShouldReturnEmptyForHugeFiles() throws IOException {
        when(ftpClient.listFiles(any(), any())).thenReturn(
                new FTPFile[]{createFTPFile("Foo.pdf", now(), 10_000_001L)}
        );
        final var file = new FTPFile();
        file.setName("Foo.pdf");
        file.setSize(0);
        assertTrue(repository.awaitUploadCompletion(file).isEmpty());
    }

    /**
     * @return an {@link Instant} which for sure will be accepted as "newer" than the server start time in {@link OperationRemoteRepository} by adding enough time.
     */
    private Instant nextPolledInstant() {
        return now().plus(1, ChronoUnit.SECONDS);
    }
}
