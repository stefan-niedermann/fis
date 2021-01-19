package it.niedermann.fis.parser;

import it.niedermann.fis.OperationInformationDto;

public interface OperationFaxParser {
    OperationInformationDto parse(String input);

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
