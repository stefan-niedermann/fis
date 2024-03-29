package it.niedermann.fis;

import it.niedermann.fis.main.model.ClientConfigurationDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@SuppressWarnings("SpellCheckingInspection")
@ConfigurationProperties("fis")
@Validated
public record FisConfiguration(
        ClientConfigurationDto client
) {
}
