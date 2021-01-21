package it.niedermann.fis.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

    private final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @MessageMapping("/register")
    public void start(StompHeaderAccessor stompHeaderAccessor) {
        final var listener = stompHeaderAccessor.getSessionId();
        logger.info("\uD83D\uDC64 Registered new socket client: " + listener);
    }
}
