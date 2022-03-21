package it.niedermann.fis.weather;

import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@SuppressWarnings("SpellCheckingInspection")
@ConfigurationProperties("fis.weather")
@Validated
public record WeatherConfiguration(
                @Length(min = 32, max = 32) String key,
                @Length(min = 2, max = 2) @NotBlank String lang,
                @NotBlank String units,
                @NotBlank String location,
                @Min(1_000) long pollInterval) {
}
