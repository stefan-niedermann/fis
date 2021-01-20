package it.niedermann.fis.weather.provider;

import it.niedermann.fis.weather.WeatherInformationDto;

import java.io.IOException;

public interface WeatherProvider {

    WeatherInformationDto fetchWeather() throws IOException;
}
