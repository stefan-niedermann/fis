package it.niedermann.fis.websocket;

import it.niedermann.fis.weather.WeatherDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class WebSocketController {

    private final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    private final WeatherDispatcher weatherDispatcher;

    public WebSocketController(WeatherDispatcher weatherDispatcher) {
        this.weatherDispatcher = weatherDispatcher;
    }

    @MessageMapping("/register")
    public void start(StompHeaderAccessor stompHeaderAccessor) throws IOException {
        final var listener = stompHeaderAccessor.getSessionId();
        logger.info("Registered new socket client: " + listener);
        weatherDispatcher.pollWeather(true);
    }
}
