package it.niedermann.fis.operation.parser;

import it.niedermann.fis.main.model.OperationDto;

interface OperationParser {
    OperationDto parse(String input) throws IllegalArgumentException;
}
