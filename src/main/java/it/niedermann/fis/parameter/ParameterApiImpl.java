package it.niedermann.fis.parameter;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.api.ParameterApi;
import it.niedermann.fis.main.model.ClientConfigurationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ParameterApiImpl implements ParameterApi {

    private final ClientConfigurationDto dto;

    public ParameterApiImpl(FisConfiguration config) {
        this.dto = config.getClient();
    }

    @Override
    public ResponseEntity<ClientConfigurationDto> getParameter(String ifNoneMatch) {
        return ResponseEntity.ok(dto);
    }
}
