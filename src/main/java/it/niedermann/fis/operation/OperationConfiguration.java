package it.niedermann.fis.operation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@SuppressWarnings("SpellCheckingInspection")
@ConfigurationProperties("fis.operation")
@Validated
public record OperationConfiguration(
                long duration,
                String origin) {
}
