package it.niedermann.fis.weather.provider.org.openweathermap;

class OpenWeatherMapResponseDto {

    Weather[] weather;
    Main main;
    Sys sys;

    static class Weather {
        String id;
    }

    static class Main {
        float temp;
    }

    static class Sys {
        long sunrise;
        long sunset;
    }
}
