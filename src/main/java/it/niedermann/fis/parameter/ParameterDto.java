package it.niedermann.fis.parameter;

public class ParameterDto {

    public final WeatherParameterDto weather = new WeatherParameterDto();
    public final OperationParameterDto operation = new OperationParameterDto();

    public static class WeatherParameterDto {
        public String key;
        public String lang;
        public String location;
        public String units;
        public long pollInterval;
    }

    public static class OperationParameterDto {
        public long duration;
        public String highlight;
    }
}
