package it.niedermann.fis.operation.remote;

import it.niedermann.fis.FisConfiguration;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;

@Service
public class OperationRemoteRepository {

    private final Logger logger = LoggerFactory.getLogger(OperationRemoteRepository.class);

    private final FisConfiguration config;
    private final FTPClient ftpClient;
    private boolean firstPoll = true;
    private String alreadyExistingFileName = "";

    public OperationRemoteRepository(
            FisConfiguration config,
            OperationFTPClientFactory ftpClientFactory
    ) throws IOException {
        this.config = config;
        this.ftpClient = ftpClientFactory.createFTPClient(config);
    }

    public Optional<FTPFile> poll() {
        if ("".equals(alreadyExistingFileName)) {
            logger.debug("Checking FTP server for incoming operations");
        } else {
            logger.debug("Checking FTP server for incoming operations (excluding \"" + alreadyExistingFileName + "\")");
        }
        try {
            final var files = ftpClient.listFiles(config.ftp().path());
            Arrays.stream(files).forEach(file -> logger.trace("⇒ [" + file.getTimestamp().getTimeInMillis() + "] " + file.getName()));
            final var match = Arrays.stream(files)
                    .filter(FTPFile::isFile)
                    .filter(file -> file.getName().endsWith(config.ftp().fileSuffix()))
                    .filter(file -> file.getSize() < config.ftp().maxFileSize())
                    .filter(file -> !Objects.equals(alreadyExistingFileName, file.getName()))
                    .sorted(Comparator
                            .comparing(FTPFile::getName)
                            .reversed())
                    .sorted(Comparator
                            .<FTPFile>comparingLong(file -> file.getTimestamp().getTimeInMillis())
                            .reversed())
                    .limit(1)
                    .findFirst();
            match.ifPresent(ftpFile -> alreadyExistingFileName = ftpFile.getName());
            if (firstPoll) {
                firstPoll = false;
                match.ifPresentOrElse(
                        ftpFile -> logger.info("Ignoring existing operation when polling the first time: " + ftpFile.getName()),
                        () -> logger.info("No operation was present when polling the first time.")
                );
                return empty();
            }
            match.ifPresentOrElse(
                    ftpFile -> logger.info("🚒 New incoming operation detected: " + ftpFile.getName()),
                    () -> logger.debug("→ No new file with suffix \"" + config.ftp().fileSuffix() + "\" is present at the server.")
            );
            return match;
        } catch (IOException e) {
            logger.error("Could not list files", e);
            return empty();
        }
    }

    public Optional<FTPFile> awaitUploadCompletion(FTPFile ftpFile) {
        logger.debug("Waiting for " + ftpFile.getName() + " being uploaded completely");
        try {
            int attempt = 0;
            long lastSize;
            long newSize = ftpFile.getSize();
            do {
                if (attempt >= config.ftp().checkUploadCompleteMaxAttempts()) {
                    throw new InterruptedException("Exceeded " + config.ftp().checkUploadCompleteMaxAttempts() + " attempts");
                }
                attempt++;
                Thread.sleep(config.ftp().checkUploadCompleteInterval());
                lastSize = newSize;
                final var polledFileOptional = Arrays.stream(ftpClient.listFiles(config.ftp().path(), fetchedFtpFile -> Objects.equals(fetchedFtpFile.getName(), ftpFile.getName()))).findFirst();
                if (polledFileOptional.isPresent()) {
                    final var polledFile = polledFileOptional.get();
                    if (polledFile.getSize() == lastSize) {
                        break;
                    }
                    logger.debug("→ [" + attempt + " / " + config.ftp().checkUploadCompleteMaxAttempts() + "] File size changed: " + byteCountToDisplaySize(lastSize) + " → " + byteCountToDisplaySize(polledFile.getSize()));
                    newSize = polledFile.getSize();
                    if (newSize > config.ftp().maxFileSize()) {
                        throw new IOException("File size is bigger than an usual operation fax: " + newSize);
                    }
                } else {
                    return empty();
                }
            } while (newSize > lastSize);
            logger.debug("→ Upload complete, total file size: " + byteCountToDisplaySize(lastSize));
            return Optional.of(ftpFile);
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage(), e);
            return empty();
        }
    }

    public Optional<File> download(FTPFile source) {
        logger.debug("Start downloading \"" + source.getName() + "\" into temporary file");
        final File target;
        try {
            target = File.createTempFile("operation-", ".pdf");
            logger.trace("→ Created temporary file: " + target.getName());

            try (final var outputStream = new BufferedOutputStream(new FileOutputStream(target))) {
                if (ftpClient.retrieveFile(config.ftp().path() + "/" + source.getName(), outputStream)) {
                    logger.debug("→ Download successful: " + target.getName());
                    return Optional.of(target);
                } else {
                    throw new IOException("Retrieving file failed");
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                if (!target.delete()) {
                    logger.warn("Could not delete downloaded file: " + target.getName(), e);
                }
            }
        } catch (IOException e) {
            logger.error("Could not create temporary file", e);
        }
        return empty();
    }
}
