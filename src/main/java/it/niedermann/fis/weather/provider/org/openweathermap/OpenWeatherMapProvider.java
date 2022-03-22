package it.niedermann.fis.weather.provider.org.openweathermap;

import it.niedermann.fis.main.model.WeatherDto;
import it.niedermann.fis.weather.provider.WeatherProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.Instant;

public class OpenWeatherMapProvider implements WeatherProvider {

    private final Logger logger = LoggerFactory.getLogger(OpenWeatherMapProvider.class);

    private final String lang;
    private final String location;
    private final String units;
    private final String key;

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private final OpenWeatherMapService service = retrofit.create(OpenWeatherMapService.class);

    public OpenWeatherMapProvider(String lang, String location, String units, String apiKey) {
        this.lang = lang;
        this.location = location;
        this.units = units;
        this.key = apiKey;
    }

    @Override
    public WeatherDto fetchWeather() throws IOException {
        OpenWeatherMapResponseDto dto;
        try {
            final var response = service.fetchWeather(lang, Long.parseLong(location), units, key).execute();
            logger.debug("Requested weather: " + response.raw().request().url());
            dto = response.body();
        } catch (NumberFormatException e) {
            final var response = service.fetchWeather(lang, location, units, key).execute();
            logger.debug("Requested weather: " + response.raw().request().url());
            dto = response.body();
        }
        return fromOpenWeatherMapResponseDto(dto);
    }

    private WeatherDto fromOpenWeatherMapResponseDto(OpenWeatherMapResponseDto response) {
        if (response == null) {
            return null;
        }
        final var dto = new WeatherDto();
        dto.setTemperature(response.main.temp);
        if (response.sys == null) {
            dto.setIsDay(true);
        } else {
            dto.setIsDay(isDay(response.sys));
        }
        if (response.weather != null && response.weather.length > 0) {
            dto.setIcon(OpenWeatherMapIconMappingUtil.get(response.weather[0].id, dto.getIsDay()));
        }
        return dto;
    }

    private boolean isDay(OpenWeatherMapResponseDto.Sys sys) {
        final var now = Instant.now();
        return now.isAfter(Instant.ofEpochMilli(sys.sunrise * 1_000)) && now.isBefore(Instant.ofEpochMilli(sys.sunset * 1_000));
    }
}
