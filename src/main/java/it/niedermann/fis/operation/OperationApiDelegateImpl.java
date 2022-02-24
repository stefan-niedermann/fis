package it.niedermann.fis.operation;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.api.OperationApiDelegate;
import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.parser.OperationParserRepository;
import it.niedermann.fis.operation.remote.OperationRemoteRepository;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OperationApiDelegateImpl implements OperationApiDelegate {

    private static final Logger logger = LoggerFactory.getLogger(OperationApiDelegateImpl.class);

    private final FisConfiguration config;
    private final OperationRemoteRepository operationRemoteRepository;
    private final OperationParserRepository operationParserRepository;

    private Thread cancelCurrentOperation;
    private OperationDto currentOperation;
    private boolean processing = false;

    public OperationApiDelegateImpl(
            FisConfiguration config,
            OperationRemoteRepository operationRemoteRepository,
            OperationParserRepository operationParserRepository
    ) {
        this.config = config;
        this.operationRemoteRepository = operationRemoteRepository;
        this.operationParserRepository = operationParserRepository;
    }

    @Override
    public ResponseEntity<OperationDto> getOperation(String ifNoneMatch) {
        return currentOperation == null
                ? processing
                ? ResponseEntity.accepted().build()
                : ResponseEntity.noContent().build()
                : ResponseEntity.ok(currentOperation);
    }

    @Scheduled(fixedDelayString = "${fis.ftp.pollInterval}")
    public void pollOperations() {
        checkForNewFTPFile().ifPresent(this::downloadAndProcessFTPFile);
    }

    private Optional<FTPFile> checkForNewFTPFile() {
        final var optionalFtpFile = operationRemoteRepository.poll();

        optionalFtpFile.ifPresentOrElse(
                ftpFile -> logger.info("🚒 New incoming PDF detected: " + ftpFile.getName()),
                () -> logger.debug("→ No new file with suffix \"" + config.getFtp().getFileSuffix() + "\" is present at the server.")
        );

        return optionalFtpFile;
    }

    private void downloadAndProcessFTPFile(FTPFile ftpFile) {
        operationRemoteRepository.download(ftpFile).ifPresent(localFile -> {
            this.processing = true;

            this.operationParserRepository.parse(localFile).ifPresent(dto -> {
                logger.trace("→ Saving operation as currently active operation: \"" + dto.getKeyword() + "\"…");
                this.currentOperation = dto;

                logger.trace("→ Planning cancellation of currently active operation: \"" + dto.getKeyword() + "\"…");
                scheduleOperationCancellation(dto);
            });

            if (!localFile.delete()) {
                logger.warn("🚒 → Could not delete downloaded FTP file: " + localFile.getName());
            }

            this.processing = false;
        });
    }

    private void scheduleOperationCancellation(OperationDto dto) {
        if (cancelCurrentOperation != null) {
            logger.trace("→ Interrupt existing operation cancellation attempt");
            cancelCurrentOperation.interrupt();
        }

        cancelCurrentOperation = new Thread(() -> {
            try {
                logger.trace("Scheduled cancellation of operation \"" + dto.getKeyword() + "\" in " + config.getOperation().getDuration() / 1_000 + "s");
                Thread.sleep(config.getOperation().getDuration());
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                logger.info("⏰ Timeout over… unset active operation \"" + dto.getKeyword() + "\"");
                this.currentOperation = null;
            } catch (InterruptedException e) {
                logger.trace("→ Existing operation " + "\"" + dto.getKeyword() + "\"" + " cancellation attempt has been interrupted.");
            }
        });
        cancelCurrentOperation.start();
        logger.trace("→ Cancellation of currently active operation: \"" + dto.getKeyword() + "\" planned.");
    }
}
