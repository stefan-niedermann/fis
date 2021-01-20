package it.niedermann.fis.weather.provider.openweathermap;

class OpenWeatherMapResponseDto {

    Weather[] weather;
    Main main;
    Sys sys;

    class Weather {
        String id;
    }

    class Main {
        float temp;
    }

    class Sys {
        long sunrise;
        long sunset;
    }
}
