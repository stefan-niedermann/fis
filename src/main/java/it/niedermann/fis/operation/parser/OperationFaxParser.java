package it.niedermann.fis.operation.parser;

import it.niedermann.fis.main.model.OperationDto;

interface OperationFaxParser {
    OperationDto parse(String input) throws IllegalArgumentException;
}
