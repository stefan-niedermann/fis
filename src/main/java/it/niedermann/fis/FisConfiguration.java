package it.niedermann.fis;

import it.niedermann.fis.main.model.ClientConfigurationDto;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@SuppressWarnings("SpellCheckingInspection")
@ConfigurationProperties("fis")
@Validated
public class FisConfiguration {

    private TesseractConfiguration tesseract = new TesseractConfiguration();
    private WeatherConfiguration weather = new WeatherConfiguration();
    private FtpConfiguration ftp = new FtpConfiguration();
    private OperationConfiguration operation = new OperationConfiguration();
    private ClientConfigurationDto client = new ClientConfigurationDto();

    public TesseractConfiguration getTesseract() {
        return tesseract;
    }

    public void setTesseract(TesseractConfiguration tesseract) {
        this.tesseract = tesseract;
    }

    public WeatherConfiguration getWeather() {
        return weather;
    }

    public void setWeather(WeatherConfiguration weather) {
        this.weather = weather;
    }

    public FtpConfiguration getFtp() {
        return ftp;
    }

    public void setFtp(FtpConfiguration ftp) {
        this.ftp = ftp;
    }

    public OperationConfiguration getOperation() {
        return operation;
    }

    public void setOperation(OperationConfiguration operation) {
        this.operation = operation;
    }

    public ClientConfigurationDto getClient() {
        return client;
    }

    public void setClient(ClientConfigurationDto client) {
        this.client = client;
    }

    public static class TesseractConfiguration {
        private String tessdata;
        @Length(min = 3, max = 3)
        @NotBlank
        private String lang;

        public String getTessdata() {
            return tessdata;
        }

        public void setTessdata(String tessdata) {
            this.tessdata = tessdata;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }
    }

    public static class WeatherConfiguration {
        @Length(min = 32, max = 32)
        private String key;
        @Length(min = 2, max = 2)
        @NotBlank
        private String lang;
        @NotBlank
        private String units;
        @NotBlank
        private String location;
        @Min(100)
        private long pollInterval;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public long getPollInterval() {
            return pollInterval;
        }

        public void setPollInterval(long pollInterval) {
            this.pollInterval = pollInterval;
        }
    }

    public static class FtpConfiguration {
        @NotBlank
        private String host;
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        private String path;
        @NotNull
        private String fileSuffix;
        @Min(1_000)
        private long pollInterval;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getFileSuffix() {
            return fileSuffix;
        }

        public void setFileSuffix(String fileSuffix) {
            this.fileSuffix = fileSuffix;
        }

        public long getPollInterval() {
            return pollInterval;
        }

        public void setPollInterval(long pollInterval) {
            this.pollInterval = pollInterval;
        }
    }

    public static class OperationConfiguration {
        private long duration;

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }
}
