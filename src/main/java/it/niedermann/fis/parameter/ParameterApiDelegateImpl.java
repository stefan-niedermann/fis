package it.niedermann.fis.parameter;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.api.ParameterApiDelegate;
import it.niedermann.fis.main.model.ParameterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

@Service
@CrossOrigin(origins = "http://localhost:4200")
public class ParameterApiDelegateImpl implements ParameterApiDelegate {

    private final ParameterDto dto = new ParameterDto();

    public ParameterApiDelegateImpl(FisConfiguration config) {
        dto.setHighlight(config.getOperation().getHighlight());
    }

    @Override
    public ResponseEntity<Object> parameterGet() {
        return ResponseEntity.ok(dto);
    }
}
