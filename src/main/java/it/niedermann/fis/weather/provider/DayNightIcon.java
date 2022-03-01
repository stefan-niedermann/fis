package it.niedermann.fis.weather.provider;

import it.niedermann.fis.main.model.WeatherIconDto;

public record DayNightIcon(
        WeatherIconDto day,
        WeatherIconDto night
) {
}