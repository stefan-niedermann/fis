package it.niedermann.fis.operation.remote;

import it.niedermann.fis.FisConfiguration;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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

    @BeforeEach
    public void setup() throws IOException {
        final var ftpConfig = mock(FisConfiguration.FtpConfiguration.class);
        when(ftpConfig.fileSuffix()).thenReturn(".pdf");
        when(ftpConfig.checkUploadCompleteInterval()).thenReturn(0L);
        when(ftpConfig.checkUploadCompleteMaxAttempts()).thenReturn(10);
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
    public void shouldReturnEmptyIfAnIORelatedErrorOccurs() throws IOException {
        when(ftpClient.listFiles(any())).thenThrow(new IOException());
        assertTrue(repository.poll().isEmpty());
    }

    @Test
    public void shouldReturnEmptyIfNoFileIsPresent() throws IOException {
        doFirstPoll();

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
    public void shouldFilterFolders() throws IOException {
        doFirstPoll();

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[0]);
        assertTrue(repository.poll().isEmpty());
    }

    @Test
    public void shouldNotReturnTheSameFileMultipleTimesAfterFirstPoll() throws IOException {
        doFirstPoll();

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Foo.pdf", now())
        });

        assertTrue(repository.poll().isPresent());
        assertTrue(repository.poll().isEmpty());
    }

    @Test
    public void shouldNotReturnAlreadyExistingFilesWhenPollingMultipleTimes() throws IOException {
        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Foo.pdf", now())
        });

        assertTrue(repository.poll().isEmpty());
        assertTrue(repository.poll().isEmpty());
    }

    @Test
    public void shouldReturnOnlyFilesWhichHaveBeenAddedAfterPollingStart() throws IOException {
        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Foo.pdf", now().minus(3, ChronoUnit.MINUTES))
        });
        final var existingFtpFile = repository.poll();
        assertTrue(existingFtpFile.isEmpty());

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Bar.pdf", now())
        });
        final var addedFtpFile = repository.poll();
        assertTrue(addedFtpFile.isPresent());
        assertEquals("Bar.pdf", addedFtpFile.get().getName());
    }

    @Test
    public void shouldFilterBySuffix() throws IOException {
        doFirstPoll();

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Foo.doc", now().minus(3, ChronoUnit.MINUTES))
        });
        assertTrue(repository.poll().isEmpty());
    }

    @Test
    public void shouldReturnTheMostCurrentFile() throws IOException {
        doFirstPoll();

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Foo.pdf", now()),
                createFTPFile("Bar.pdf", now().minus(3, ChronoUnit.MINUTES))
        });
        final var ftpFile1 = repository.poll();
        assertTrue(ftpFile1.isPresent());
        assertEquals("Foo.pdf", ftpFile1.get().getName());

        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[]{
                createFTPFile("Foo.pdf", now().minus(3, ChronoUnit.MINUTES)),
                createFTPFile("Bar.pdf", now())
        });
        final var ftpFile2 = repository.poll();
        assertTrue(ftpFile2.isPresent());
        assertEquals("Bar.pdf", ftpFile2.get().getName());
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
        assertTrue(repository.waitForUploadCompletion(file).isPresent());
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
        assertTrue(repository.waitForUploadCompletion(file).isPresent());
    }

    @Test
    public void waitForUploadCompletionShouldStopAfterSomeAttempts() throws IOException {
        when(ftpClient.listFiles(any(), any())).thenReturn(
                new FTPFile[]{createFTPFile("Foo.pdf", now(), 20)},
                IntStream
                        .rangeClosed(3, 15)
                        .boxed()
                        .map(val -> val * 10)
                        .map(size -> new FTPFile[]{createFTPFile("Foo.pdf", now(), size)})
                        .toArray(FTPFile[][]::new)
        );
        final var file = new FTPFile();
        file.setName("Foo.pdf");
        file.setSize(10);
        assertTrue(repository.waitForUploadCompletion(file).isEmpty());
    }

    // TODO Test file size 0 at beginning

    @Test
    public void waitForUploadCompletionShouldGracefullyHandleErrors() throws IOException {
        when(ftpClient.listFiles(any(), any())).thenThrow(new IOException());
        final var file = new FTPFile();
        file.setName("Foo.pdf");
        file.setSize(111);
        assertTrue(repository.waitForUploadCompletion(file).isEmpty());
    }

    private void doFirstPoll() throws IOException {
        when(ftpClient.listFiles(any())).thenReturn(new FTPFile[0]);
        repository.poll();
    }
}
