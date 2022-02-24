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
                return empty();
            }
            return match;
        } catch (IOException e) {
            logger.error("Could not list files", e);
            return empty();
        }
    }

    public Optional<File> download(FTPFile source) {
        final File target;
        try {
            target = File.createTempFile("operation-", ".pdf");
            logger.debug("→ Created temporary file: " + target.getName());

            try (final var outputStream = new BufferedOutputStream(new FileOutputStream(target))) {
                if (ftpClient.retrieveFile(config.getFtp().getPath() + "/" + source.getName(), outputStream)) {
                    return Optional.of(target);
                } else {
                    logger.warn("🚒 → Could not download new FTP file!");
                    if (!target.delete()) {
                        logger.warn("🚒 → Could not delete downloaded FTP file: " + target.getName());
                    }
                }
            } catch (IOException e) {
                if (!target.delete()) {
                    logger.warn("🚒 → Could not delete downloaded FTP file: " + target.getName(), e);
                }
            }
        } catch (IOException e) {
            logger.error("Could not create temporary file", e);
        }
        return empty();
    }
}
