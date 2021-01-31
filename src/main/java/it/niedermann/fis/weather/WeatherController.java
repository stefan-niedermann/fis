package it.niedermann.fis.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class WeatherController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    private final WeatherDispatcher dispatcher;

    public WeatherController(WeatherDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @GetMapping("/weather")
    public ResponseEntity<WeatherDto> pollWeather(HttpServletRequest request) throws IOException {
        final var weather = dispatcher.getCurrentWeather();
        logger.info("Client IP Address: ", request.getRemoteAddr());
        logger.info("⛅ Weather info got polled: " + weather.temperature + "°");
        return ResponseEntity.ok(weather);
    }
}
