package it.niedermann.fis.operation.remote.ftp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@SuppressWarnings("SpellCheckingInspection")
@ConfigurationProperties("fis.ftp")
@Validated
public record FtpConfiguration(
                @NotBlank String username,
                @NotBlank String password,
                @NotBlank String host,
                String path,
                @NotNull String fileSuffix,
                @Min(100) long pollInterval,
                @Min(100) long checkUploadCompleteInterval,
                @Min(0) int checkUploadCompleteMaxAttempts,
                @Min(0) long maxFileSize) {
}
