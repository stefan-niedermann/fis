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

@Service
public class OperationRemoteRepository {

    private static final Logger logger = LoggerFactory.getLogger(OperationRemoteRepository.class);

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
        logger.debug("Checking FTP server for incoming operations");
        try {
            final var match = Arrays.stream(ftpClient.listFiles(config.getFtp().getPath()))
                    .filter(FTPFile::isFile)
                    .filter(file -> file.getName().endsWith(config.getFtp().getFileSuffix()))
                    .sorted(Comparator
                            .<FTPFile>comparingLong(file -> file.getTimestamp().getTimeInMillis())
                            .reversed())
                    .limit(1)
                    .filter(file -> !Objects.equals(alreadyExistingFileName, file.getName()))
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
                    () -> logger.debug("→ No new file with suffix \"" + config.getFtp().getFileSuffix() + "\" is present at the server.")
            );
            return match;
        } catch (IOException e) {
            logger.error("Could not list files", e);
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
                if (ftpClient.retrieveFile(config.getFtp().getPath() + "/" + source.getName(), outputStream)) {
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
