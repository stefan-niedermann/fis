package it.niedermann.fis.websocket;

import it.niedermann.fis.weather.WeatherDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

    private final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    private final WeatherDispatcher weatherDispatcher;

    public WebSocketController(WeatherDispatcher weatherDispatcher) {
        this.weatherDispatcher = weatherDispatcher;
    }

    @MessageMapping("/register")
    public void start(StompHeaderAccessor stompHeaderAccessor) {
        final var listener = stompHeaderAccessor.getSessionId();
        logger.info("Registered new socket client: " + listener);
        weatherDispatcher.pushOnRegister(listener);
        // Sometimes the direct push seems to be a bit too early, so make sure every client receives the current weather soon.
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                weatherDispatcher.pushOnRegister(listener);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
