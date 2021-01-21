package it.niedermann.fis.weather;

import it.niedermann.fis.weather.provider.WeatherProvider;
import it.niedermann.fis.weather.provider.openweathermap.OpenWeatherMapProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
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
        final var newWeatherInformation = weatherProvider.fetchWeather();

        if (Objects.equals(newWeatherInformation, lastWeatherInformation)) {
            logger.debug("Skip weather broadcast because it didn't change.");
        } else {
            lastWeatherInformation = newWeatherInformation;
            template.convertAndSend("/notification/weather", lastWeatherInformation);
            logger.info("⛅ Broadcast weather information: " + lastWeatherInformation.temperature + "°");
        }
    }

    public void pushOnRegister(String listener) {
        if (lastWeatherInformation == null) {
            logger.debug("Skip sending weather to \"" + listener + "\" because it is not there yet.");
        } else {
            final SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(listener);
            headerAccessor.setLeaveMutable(true);
            template.convertAndSendToUser(listener, "/notification/weather", lastWeatherInformation,
                    headerAccessor.getMessageHeaders());
            logger.info("⛅ Sending weather information to \"" + listener + "\": " + lastWeatherInformation.temperature + "°");
        }
    }
}
