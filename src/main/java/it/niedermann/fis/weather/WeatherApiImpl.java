package it.niedermann.fis.weather;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.api.WeatherApi;
import it.niedermann.fis.main.model.WeatherDto;
import it.niedermann.fis.weather.provider.WeatherProvider;
import it.niedermann.fis.weather.provider.openweathermap.OpenWeatherMapProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Objects;

@Controller
@RequestMapping("/api")
public class WeatherApiImpl implements WeatherApi {

    private final Logger logger = LoggerFactory.getLogger(WeatherApiImpl.class);

    private final WeatherProvider weatherProvider;
    private WeatherDto weather;

    public WeatherApiImpl(
            FisConfiguration config
    ) {
        if (config.getWeather().getKey() == null) {
            weatherProvider = null;
            logger.info("Weather information is not available because no API key has been specified");
        } else {
            weatherProvider = new OpenWeatherMapProvider(config.getWeather().getLang(),
                    config.getWeather().getLocation(),
                    config.getWeather().getUnits(),
                    config.getWeather().getKey());
        }
    }

    @Override
    public ResponseEntity<WeatherDto> getWeather(String ifNoneMatch) {
        if (weatherProvider == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            if (weather == null) {
                pollWeather();
            }
            return ResponseEntity.ok(weather);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Scheduled(fixedDelayString = "${fis.weather.pollInterval}")
    public void pollWeather() throws IOException {
        if (weatherProvider == null) {
            return;
        }

        final var newWeatherInformation = weatherProvider.fetchWeather();

        if (Objects.equals(newWeatherInformation, weather)) {
            logger.debug("Skip weather broadcast because it didn't change.");
        } else {
            weather = newWeatherInformation;
            logger.info("⛅ Broadcast weather information: " + weather.getTemperature() + "°");
        }
    }
}
