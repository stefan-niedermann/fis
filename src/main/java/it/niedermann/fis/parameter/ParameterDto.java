package it.niedermann.fis.parameter;

public class ParameterDto {

    public final OperationParameterDto operation = new OperationParameterDto();

    public static class OperationParameterDto {
        public long duration;
        public String highlight;
    }
}
