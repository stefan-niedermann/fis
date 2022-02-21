package it.niedermann.fis.weather.provider;

import it.niedermann.fis.main.model.WeatherDto;

import java.io.IOException;

public interface WeatherProvider {

    WeatherDto fetchWeather() throws IOException;
}
