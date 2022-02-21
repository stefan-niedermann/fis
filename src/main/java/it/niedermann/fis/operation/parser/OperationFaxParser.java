package it.niedermann.fis.operation.parser;

import it.niedermann.fis.main.model.OperationDto;

public interface OperationFaxParser {
    OperationDto parse(String input) throws IllegalArgumentException;

    static OperationFaxParser create(String type) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (type) {
            case "mittelfranken-sued":
                return new MittelfrankenSuedParser();
            default:
                throw new IllegalArgumentException("Could not find a " + OperationFaxParser.class.getSimpleName() + " for type \"" + type + "\"");
        }
    }
}
