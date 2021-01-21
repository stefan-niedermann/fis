package it.niedermann.fis.weather;

import it.niedermann.fis.weather.provider.WeatherProvider;
import it.niedermann.fis.weather.provider.openweathermap.OpenWeatherMapProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
            @Value("${weather.lang}") String lang,
            @Value("${weather.location}") String location,
            @Value("${weather.units}") String units,
            @Value("${weather.key}") String key
    ) {
        this.template = template;
        this.weatherProvider = new OpenWeatherMapProvider(lang, location, units, key);
    }

    @Scheduled(fixedDelayString = "${weather.poll.interval}")
    public void pollWeather() throws IOException {
        pollWeather(false);
    }

    /**
     * @param pushOnNoChange if {@code true}, this method will push the new information to users even though it did not change.
     */
    public void pollWeather(boolean pushOnNoChange) throws IOException {
        logger.debug("PushOnNoChange: " + pushOnNoChange);

        final var newWeatherInformation = weatherProvider.fetchWeather();

        if (pushOnNoChange || !Objects.equals(newWeatherInformation, lastWeatherInformation)) {
            lastWeatherInformation = newWeatherInformation;
            template.convertAndSend("/notification/weather", lastWeatherInformation);
            logger.info("⛅ Broadcast weather information: " + lastWeatherInformation.temperature + "°");
        } else {
            logger.debug("Skip weather push because it didn't change.");
        }
    }
}
