package it.niedermann.fis.socket;

import it.niedermann.fis.operation.OperationDispatcher;
import it.niedermann.fis.weather.WeatherDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SocketController {

    private final Logger logger = LoggerFactory.getLogger(SocketController.class);

    private final OperationDispatcher operationDispatcher;
    private final WeatherDispatcher weatherDispatcher;
    private final SocketRegistry socketRegistry;

    public SocketController(
            OperationDispatcher operationDispatcher,
            WeatherDispatcher weatherDispatcher,
            SocketRegistry socketRegistry
    ) {
        this.operationDispatcher = operationDispatcher;
        this.weatherDispatcher = weatherDispatcher;
        this.socketRegistry = socketRegistry;
    }

    @MessageMapping("/register")
    public void start(StompHeaderAccessor stompHeaderAccessor) throws IOException {
        logger.info("Registered new socket client: " + stompHeaderAccessor.getSessionId());
        socketRegistry.add(stompHeaderAccessor.getSessionId());
        weatherDispatcher.dispatch();
        operationDispatcher.dispatch();
    }

    @MessageMapping("/unregister")
    public void stop(StompHeaderAccessor stompHeaderAccessor) {
        logger.info("Unregistered socket client: " + stompHeaderAccessor.getSessionId());
        socketRegistry.remove(stompHeaderAccessor.getSessionId());
    }
}
