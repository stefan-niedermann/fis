package it.niedermann.fis.weather;

import it.niedermann.fis.socket.SocketRegistry;
import it.niedermann.fis.weather.provider.WeatherProvider;
import it.niedermann.fis.weather.provider.openweathermap.OpenWeatherMapProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;

@Service
public class WeatherDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(WeatherDispatcher.class);

    @Autowired
    private SocketRegistry socketRegistry;
    private final WeatherProvider weatherProvider;
    private WeatherInformationDto lastWeatherInformation;
    private final SimpMessagingTemplate template;

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
    public void dispatch() throws IOException {
        final Collection<String> listeners = socketRegistry.getListeners();
        if (listeners.size() == 0) {
            logger.info("Skip weather poll because no listeners are registered.");
            return;
        }
        final WeatherInformationDto newWeatherInformation = weatherProvider.fetchWeather();
        if (lastWeatherInformation == null || !lastWeatherInformation.equals(newWeatherInformation)) {
            lastWeatherInformation = newWeatherInformation;
            for (String listener : listeners) {
                logger.info("Sending notification to " + listener);

                SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
                headerAccessor.setSessionId(listener);
                headerAccessor.setLeaveMutable(true);
                template.convertAndSendToUser(
                        listener,
                        "/notification/weather",
                        newWeatherInformation,
                        headerAccessor.getMessageHeaders());
            }
        }
    }
}
