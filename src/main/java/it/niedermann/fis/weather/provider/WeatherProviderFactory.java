package it.niedermann.fis.weather.provider;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.weather.provider.org.openweathermap.OpenWeatherMapProvider;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

@Service
public class WeatherProviderFactory {

    private final FisConfiguration config;

    public WeatherProviderFactory(FisConfiguration config) {
        this.config = config;
    }

    @SuppressWarnings({ "UnnecessaryDefault" })
    public WeatherProvider createWeatherProvider(WeatherProviderType type) {
        return switch (type) {
            case OPENWEATHERMAP -> new OpenWeatherMapProvider(config.weather().lang(),
                    config.weather().location(),
                    config.weather().units(),
                    config.weather().key());
            default -> throw new NotImplementedException(
                    String.format("Could not find a %s for type \"%s\"", WeatherProvider.class.getSimpleName(), type));
        };
    }
}
