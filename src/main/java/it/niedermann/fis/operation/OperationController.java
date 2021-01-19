package it.niedermann.fis.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketMessage;

import java.util.Collection;
import java.util.LinkedHashSet;

import static org.springframework.util.ObjectUtils.isEmpty;

@RestController
public class OperationController {

    private final Logger logger = LoggerFactory.getLogger(OperationController.class);
    private final SimpMessagingTemplate socketMessage;
    private final Collection<String> connectedUsers = new LinkedHashSet<>();

    public OperationController(
            SimpMessagingTemplate socketMessage,
            @Value("${ftp.host}") String ftpHost,
            @Value("${ftp.user}") String ftpUser,
            @Value("${ftp.password}") String ftpPassword,
            @Value("${ftp.path}") String ftpPath,
            @Value("${ftp.file.suffix}") String ftpFileSuffix,
            @Value("#{new Integer('${ftp.poll.interval}')}") Integer ftpPollInterval,
            @Value("${tesseract.tessdata}") String tessdataPath,
            @Value("${tesseract.lang}") String tessLang
    ) {
        this.socketMessage = socketMessage;
        if (isEmpty(tessdataPath)) {
            tessdataPath = System.getProperty("user.home") + "/tessdata";
        }
        new OperationWatcherThread(
                ftpHost, ftpUser, ftpPassword, ftpPath, ftpFileSuffix, ftpPollInterval, tessdataPath, tessLang,
                (dto) -> postNewActiveOperation(new WebSocketMessage<>() {
                    @Override
                    public OperationInformationDto getPayload() {
                        return dto;
                    }

                    @Override
                    public int getPayloadLength() {
                        return 0;
                    }

                    @Override
                    public boolean isLast() {
                        return false;
                    }
                })
        ).start();
    }

    @MessageMapping("/register")
    public void registerClient(String clientId) {
        logger.debug("Registering new client: \"" + clientId + "\"");
        connectedUsers.add(clientId);
    }

    @MessageMapping("/message")
    public void postNewActiveOperation(WebSocketMessage<OperationInformationDto> message) {
        for (String connectedUser : connectedUsers) {
            logger.debug("Posting new active operation to \"" + connectedUser + "\"");
            socketMessage.convertAndSendToUser(connectedUser, "/operation", message);
        }
    }

    @GetMapping("/operation/duration")
    ResponseEntity<Integer> getOperationDuration(
            @Value("#{new Integer('${operation.duration}')}") Integer operationDuration
    ) {
        return ResponseEntity.ok(operationDuration);
    }

    @GetMapping("/operation/highlight")
    ResponseEntity<String> getOperationHighlight(
            @Value("${operation.highlight}") String operationHighlight
    ) {
        return ResponseEntity.ok(operationHighlight);
    }
}
