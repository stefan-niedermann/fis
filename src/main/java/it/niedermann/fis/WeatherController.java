package it.niedermann.fis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherInformationDto dto = new WeatherInformationDto();

    public WeatherController(
            @Value("${weather.key}") String weatherKey,
            @Value("${weather.lang}") String weatherLang,
            @Value("${weather.location}") String weatherLocation,
            @Value("${weather.units}") String weatherUnits,
            @Value("${weather.poll.interval}") String weatherPollInterval
    ) {
        dto.key = weatherKey;
        dto.lang = weatherLang;
        dto.location = weatherLocation;
        dto.units = weatherUnits;
        dto.pollInterval = Integer.parseInt(weatherPollInterval);
    }

    @GetMapping("/weather")
    ResponseEntity<WeatherInformationDto> getWeatherInformation() {
        return ResponseEntity.ok(dto);
    }
}
