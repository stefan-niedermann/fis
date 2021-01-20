package it.niedermann.fis.parameter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParameterController {

    private final ParameterDto dto = new ParameterDto();

    public ParameterController(
            @Value("${weather.lang}") String language,
            @Value("${operation.highlight}") String operationHighlight,
            @Value("#{new Long('${operation.duration}')}") Long operationDuration
    ) {
        dto.language = language;
        dto.operation.duration = operationDuration;
        dto.operation.highlight = operationHighlight;
    }

    @GetMapping("/parameter")
    ResponseEntity<ParameterDto> getParameter() {
        return ResponseEntity.ok(dto);
    }
}
