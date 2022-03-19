package it.niedermann.fis;

import it.niedermann.fis.main.model.ClientConfigurationDto;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
@ConfigurationProperties("fis")
@Validated
public record FisConfiguration(
        String contact,
        TesseractConfiguration tesseract,
        WeatherConfiguration weather,
        FtpConfiguration ftp,
        OperationConfiguration operation,
        ClientConfigurationDto client
) {

    public static record TesseractConfiguration(
            String tessdata,
            @Length(min = 3, max = 3) @NotBlank String lang
    ) {
    }

    public static record WeatherConfiguration(
            @Length(min = 32, max = 32) String key,
            @Length(min = 2, max = 2) @NotBlank String lang,
            @NotBlank String units,
            @NotBlank String location,
            @Min(1_000) long pollInterval
    ) {
    }

    public static record FtpConfiguration(
            @NotBlank String host,
            @NotBlank String username,
            @NotBlank String password,
            String path,
            @NotNull String fileSuffix,
            @Min(100) long pollInterval,
            @Min(100) long checkUploadCompleteInterval,
            @Min(0) int checkUploadCompleteMaxAttempts,
            @Min(0) long maxFileSize
    ) {
    }

    public static record OperationConfiguration(
            long duration,
            String location,
            List<String> recipients,
            String sender
    ) {
    }
}
