package it.niedermann.fis.parameter;

import it.niedermann.fis.FisConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ParameterController {

    private final ParameterDto dto = new ParameterDto();

    public ParameterController(FisConfiguration config) {
        dto.language = config.getWeather().getLang();
        dto.operation.duration = config.getOperation().getDuration();
        dto.operation.highlight = config.getOperation().getHighlight();
    }

    @GetMapping("/parameter")
    ResponseEntity<ParameterDto> getParameter() {
        return ResponseEntity.ok(dto);
    }
}
