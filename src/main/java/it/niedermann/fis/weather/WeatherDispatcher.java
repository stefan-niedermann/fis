package it.niedermann.fis.weather;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.weather.provider.WeatherProvider;
import it.niedermann.fis.weather.provider.openweathermap.OpenWeatherMapProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
public class WeatherDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(WeatherDispatcher.class);

    private final WeatherProvider weatherProvider;
    private final SimpMessagingTemplate template;
    private WeatherDto lastWeatherInformation;

    public WeatherDispatcher(
            SimpMessagingTemplate template,
            FisConfiguration config
    ) {
        this.template = template;
        this.weatherProvider = new OpenWeatherMapProvider(config.getWeather().getLang(),
                config.getWeather().getLocation(),
                config.getWeather().getUnits(),
                config.getWeather().getKey());
    }

    @Scheduled(fixedDelayString = "${fis.weather.pollInterval}")
    public void pollWeather() throws IOException {
        final var newWeatherInformation = weatherProvider.fetchWeather();

        if (Objects.equals(newWeatherInformation, lastWeatherInformation)) {
            logger.debug("Skip weather broadcast because it didn't change.");
        } else {
            lastWeatherInformation = newWeatherInformation;
            template.convertAndSend("/notification/weather", lastWeatherInformation);
            logger.info("⛅ Broadcast weather information: " + lastWeatherInformation.temperature + "°");
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
