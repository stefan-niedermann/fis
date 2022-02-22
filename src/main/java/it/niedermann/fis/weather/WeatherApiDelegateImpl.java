package it.niedermann.fis.weather;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.api.WeatherApiDelegate;
import it.niedermann.fis.main.model.WeatherDto;
import it.niedermann.fis.weather.provider.WeatherProvider;
import it.niedermann.fis.weather.provider.openweathermap.OpenWeatherMapProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
public class WeatherApiDelegateImpl implements WeatherApiDelegate {

    private static final Logger logger = LoggerFactory.getLogger(WeatherApiDelegateImpl.class);

    private final WeatherProvider weatherProvider;
    private final SimpMessagingTemplate template;
    private WeatherDto lastWeatherInformation;

    public WeatherApiDelegateImpl(
            SimpMessagingTemplate template,
            FisConfiguration config
    ) {
        this.template = template;
        this.weatherProvider = new OpenWeatherMapProvider(config.getWeather().getLang(),
                config.getWeather().getLocation(),
                config.getWeather().getUnits(),
                config.getWeather().getKey());
    }

    @Override
    public ResponseEntity<WeatherDto> weatherGet() {
        try {
            final var weather = getCurrentWeather();
            logger.info("⛅ Weather info got polled: " + weather.getTemperature() + "°");
            return ResponseEntity.ok(weather);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Scheduled(fixedDelayString = "${fis.weather.pollInterval}")
    public void pollWeather() throws IOException {
        final var newWeatherInformation = weatherProvider.fetchWeather();

        if (Objects.equals(newWeatherInformation, lastWeatherInformation)) {
            logger.debug("Skip weather broadcast because it didn't change.");
        } else {
            lastWeatherInformation = newWeatherInformation;
            template.convertAndSend("/notification/weather", lastWeatherInformation);
            logger.info("⛅ Broadcast weather information: " + lastWeatherInformation.getTemperature() + "°");
        }
    }

    /**
     * @return the last cached weather information if available, a freshly fetched info otherwise.
     */
    public WeatherDto getCurrentWeather() throws IOException {
        if (lastWeatherInformation == null) {
            this.lastWeatherInformation = weatherProvider.fetchWeather();
        }
        return lastWeatherInformation;
    }
}
