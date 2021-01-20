package it.niedermann.fis.weather;

import it.niedermann.fis.socket.SocketRegistry;
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

    private final SocketRegistry socketRegistry;
    private final WeatherProvider weatherProvider;
    private WeatherDto lastWeatherInformation;
    private final SimpMessagingTemplate template;

    public WeatherDispatcher(
            SocketRegistry socketRegistry,
            SimpMessagingTemplate template,
            @Value("${weather.lang}") String lang,
            @Value("${weather.location}") String location,
            @Value("${weather.units}") String units,
            @Value("${weather.key}") String key
    ) {
        this.socketRegistry = socketRegistry;
        this.template = template;
        this.weatherProvider = new OpenWeatherMapProvider(lang, location, units, key);
    }

    @Scheduled(fixedDelayString = "${weather.poll.interval}")
    public void pollWeather() throws IOException {
        final var listeners = socketRegistry.getListeners();
        if (listeners.size() == 0) {
            logger.debug("Skip weather poll because no listeners are registered.");
            return;
        }
        final var newWeatherInformation = weatherProvider.fetchWeather();
        if (Objects.equals(newWeatherInformation, lastWeatherInformation)) {
            logger.debug("Skip weather push because it didn't change.");
        } else {
            lastWeatherInformation = newWeatherInformation;
            listeners.forEach(this::sendCurrentWeatherInformation);
        }
    }

    /**
     * Sends the last available {@link WeatherDto} to the {@param listener}, as long as it is not {@code null}
     */
    public void sendCurrentWeatherInformation(String listener) {
        if (lastWeatherInformation != null) {
            logger.info("⛅ Sending weather information to \"" + listener + "\": " + lastWeatherInformation.temperature + "°");

            final var headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(listener);
            headerAccessor.setLeaveMutable(true);
            template.convertAndSendToUser(
                    listener,
                    "/notification/weather",
                    lastWeatherInformation,
                    headerAccessor.getMessageHeaders());
        } else {
            logger.debug("Did not send current weather information because it is not yet available.");
        }
    }
}
