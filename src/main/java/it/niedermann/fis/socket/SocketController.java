package it.niedermann.fis.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SocketController {

    private final Logger logger = LoggerFactory.getLogger(SocketController.class);

    @Autowired
    private SocketRegistry socketRegistry;

    @MessageMapping("/register")
    public void start(StompHeaderAccessor stompHeaderAccessor) {
        logger.error("Registered " + stompHeaderAccessor.getSessionId());
        socketRegistry.add(stompHeaderAccessor.getSessionId());
    }

    @MessageMapping("/unregister")
    public void stop(StompHeaderAccessor stompHeaderAccessor) {
        socketRegistry.remove(stompHeaderAccessor.getSessionId());
    }
}
