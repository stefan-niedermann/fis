package it.niedermann.fis.operation;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.api.OperationApi;
import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.parser.OperationParserRepository;
import it.niedermann.fis.operation.remote.ftp.OperationFTPRepository;
import it.niedermann.fis.operation.remote.mail.OperationMailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;

@Controller
@RequestMapping("/api")
public class OperationApiImpl implements OperationApi {

    private final Logger logger = LoggerFactory.getLogger(OperationApiImpl.class);

    private final FisConfiguration config;
    private final OperationFTPRepository ftpRepository;
    private final OperationMailRepository mailRepository;
    private final OperationParserRepository parserRepository;

    private Thread cancelCurrentOperation;
    private OperationDto currentOperation;
    private boolean processing = false;

    public OperationApiImpl(
            FisConfiguration config,
            OperationFTPRepository ftpRepository,
            OperationMailRepository mailRepository,
            OperationParserRepository parserRepository
    ) {
        this.config = config;
        this.ftpRepository = ftpRepository;
        this.mailRepository = mailRepository;
        this.parserRepository = parserRepository;
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
        ftpRepository.poll()
                .flatMap(ftpRepository::awaitUploadCompletion)
                .flatMap(ftpRepository::download)
                .ifPresent(this::parseAndApplyOperation);
    }

    private void parseAndApplyOperation(File operationFile) {
        this.processing = true;

        this.parserRepository.parse(operationFile).ifPresent(operationDto -> {
            logger.debug("🚒 Saving operation as currently active operation: \"" + operationDto.getKeyword() + "\"…");
            this.currentOperation = operationDto;
            mailRepository.send(operationDto);

            logger.debug("Planning cancellation of currently active operation: \"" + operationDto.getKeyword() + "\"…");
            scheduleOperationCancellation(operationDto);
        });

        if (!operationFile.delete()) {
            logger.warn("Could not delete downloaded FTP file: " + operationFile.getName());
        }

        this.processing = false;
    }

    private void scheduleOperationCancellation(OperationDto dto) {
        if (cancelCurrentOperation != null) {
            logger.trace("→ Interrupt existing operation cancellation attempt");
            cancelCurrentOperation.interrupt();
        }

        cancelCurrentOperation = new Thread(() -> {
            try {
                logger.trace("Scheduled cancellation of operation \"" + dto.getKeyword() + "\" in " + config.operation().duration() / 1_000 + "s");
                Thread.sleep(config.operation().duration());
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
