package it.niedermann.fis.weather.provider;

public record DayNightIcon(
        WeatherIconDto day,
        WeatherIconDto night
) {
}