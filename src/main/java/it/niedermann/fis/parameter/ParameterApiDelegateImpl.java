package it.niedermann.fis.parameter;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.api.ParameterApiDelegate;
import it.niedermann.fis.main.model.ClientConfigurationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ParameterApiDelegateImpl implements ParameterApiDelegate {

    private final ClientConfigurationDto dto;

    public ParameterApiDelegateImpl(FisConfiguration config) {
        this.dto = config.getClient();
    }

    @Override
    public ResponseEntity<ClientConfigurationDto> getParameter(String ifNoneMatch) {
        return ResponseEntity.ok(dto);
    }
}
