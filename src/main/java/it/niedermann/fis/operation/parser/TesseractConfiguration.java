package it.niedermann.fis.operation.parser;

import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@SuppressWarnings("SpellCheckingInspection")
@ConfigurationProperties("fis.tesseract")
@Validated
public record TesseractConfiguration(
                @Length(min = 3, max = 3) @NotBlank String lang,
                @Min(70) long dpi,
                String tessdata) {
}
