package it.niedermann.fis.weather.provider;

import it.niedermann.fis.main.model.WeatherIconDto;

public class DayNightIcon {
    private final WeatherIconDto day;
    private final WeatherIconDto night;

    public DayNightIcon(WeatherIconDto day, WeatherIconDto night) {
        this.day = day;
        this.night = night;
    }

    public WeatherIconDto getDay() {
        return day;
    }

    public WeatherIconDto getNight() {
        return night;
    }
}