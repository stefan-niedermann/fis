package it.niedermann.fis.parameter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParameterController {

    private final ParameterDto dto = new ParameterDto();

    public ParameterController(
            @Value("${weather.key}") String weatherKey,
            @Value("${weather.lang}") String weatherLang,
            @Value("${weather.location}") String weatherLocation,
            @Value("${weather.units}") String weatherUnits,
            @Value("#{new Long(${weather.poll.interval})}") Long weatherPollInterval,
            @Value("${operation.highlight}") String operationHighlight,
            @Value("#{new Long('${operation.duration}')}") Long operationDuration
    ) {
        dto.weather.key = weatherKey;
        dto.weather.lang = weatherLang;
        dto.weather.location = weatherLocation;
        dto.weather.units = weatherUnits;
        dto.weather.pollInterval = weatherPollInterval;
        dto.operation.duration = operationDuration;
        dto.operation.highlight = operationHighlight;
    }

    @GetMapping("/parameter")
    ResponseEntity<ParameterDto> getParameter() {
        return ResponseEntity.ok(dto);
    }
}
