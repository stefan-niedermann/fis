package it.niedermann.fis.weather.provider.openweathermap;

import it.niedermann.fis.weather.WeatherInformationDto;
import it.niedermann.fis.weather.provider.WeatherProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.Instant;

public class OpenWeatherMapProvider implements WeatherProvider {

    private static final Logger logger = LoggerFactory.getLogger(OpenWeatherMapProvider.class);
    private static final IconMap iconMap = new IconMap();

    private final String lang;
    private final String location;
    private final String units;
    private final String key;

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private final OpenWeatherMapService service = retrofit.create(OpenWeatherMapService.class);

    public OpenWeatherMapProvider(String lang, String location, String units, String key) {
        this.lang = lang;
        this.location = location;
        this.units = units;
        this.key = key;
    }

    @Override
    public WeatherInformationDto fetchWeather() throws IOException {
        OpenWeatherMapResponseDto dto;
        try {
            final Response<OpenWeatherMapResponseDto> response = service.fetchWeather(lang, Long.parseLong(location), units, key).execute();
            logger.debug("Requested weather: " + response.raw().request().url());
            dto = response.body();
        } catch (NumberFormatException e) {
            final Response<OpenWeatherMapResponseDto> response = service.fetchWeather(lang, location, units, key).execute();
            logger.debug("Requested weather: " + response.raw().request().url());
            dto = response.body();
        }
        return fromOpenWeatherMapResponseDto(dto);
    }

    private WeatherInformationDto fromOpenWeatherMapResponseDto(OpenWeatherMapResponseDto response) {
        if (response == null) {
            return null;
        }
        final WeatherInformationDto dto = new WeatherInformationDto();
        dto.temperature = response.main.temp;
        if (response.sys == null) {
            dto.isDay = true;
        } else {
            dto.isDay = isDay(response.sys);
        }
        if (response.weather != null && response.weather.length > 0) {
            dto.icon = getIcon(response.weather[0].id, dto.isDay);
        }
        return dto;
    }

    private String getIcon(String openWeatherMapId, boolean isDay) {
        return iconMap.get(openWeatherMapId, isDay);
    }

    private boolean isDay(OpenWeatherMapResponseDto.Sys sys) {
        final Instant now = Instant.now();
        return now.isAfter(Instant.ofEpochMilli(sys.sunrise * 1000)) && now.isBefore(Instant.ofEpochMilli(sys.sunset * 1000));
    }
}
