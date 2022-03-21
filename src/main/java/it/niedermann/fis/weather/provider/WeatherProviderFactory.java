package it.niedermann.fis.weather.provider;

import it.niedermann.fis.weather.WeatherConfiguration;
import it.niedermann.fis.weather.provider.org.openweathermap.OpenWeatherMapProvider;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

@Service
public class WeatherProviderFactory {

    private final WeatherConfiguration config;

    public WeatherProviderFactory(WeatherConfiguration config) {
        this.config = config;
    }

    @SuppressWarnings({ "UnnecessaryDefault" })
    public WeatherProvider createWeatherProvider(WeatherProviderType type) {
        return switch (type) {
            case OPENWEATHERMAP -> new OpenWeatherMapProvider(config.lang(),
                    config.location(),
                    config.units(),
                    config.key());
            default -> throw new NotImplementedException(
                    String.format("Could not find a %s for type \"%s\"", WeatherProvider.class.getSimpleName(), type));
        };
    }
}
