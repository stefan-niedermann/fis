package it.niedermann.fis.parameter;

public class ParameterDto {

    public String language;
    public final OperationParameterDto operation = new OperationParameterDto();

    public static class OperationParameterDto {
        public long duration;
        public String highlight;
    }

    @Override
    public String toString() {
        return "ParameterDto{" +
                "language='" + language + '\'' +
                ", operation.duration='" + operation.duration + '\'' +
                ", operation.highlight=" + operation.highlight +
                '}';
    }
}
