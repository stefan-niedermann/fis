package it.niedermann.fis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fis")
public class FisConfiguration {

    private TesseractConfiguration tesseract = new TesseractConfiguration();
    private WeatherConfiguration weather = new WeatherConfiguration();
    private FtpConfiguration ftp = new FtpConfiguration();
    private OperationConfiguration operation = new OperationConfiguration();

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

    public static class TesseractConfiguration {
        private String tessdata;
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
        private String key;
        private String lang;
        private String units;
        private String location;
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
        private String host;
        private String username;
        private String password;
        private String path;
        private String fileSuffix;
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
        private String highlight;

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public String getHighlight() {
            return highlight;
        }

        public void setHighlight(String highlight) {
            this.highlight = highlight;
        }
    }
}
