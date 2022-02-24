package it.niedermann.fis.operation.parser;

import org.springframework.stereotype.Service;

@Service
class OperationParserFactory {

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public OperationFaxParser createParser(Parser type) {
        switch (type) {
            case MITTELFRANKEN_SUED:
                return new MittelfrankenSuedParser();
            default:
                throw new IllegalArgumentException("Could not find a " + OperationFaxParser.class.getSimpleName() + " for type \"" + type + "\"");
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    public enum Parser {
        MITTELFRANKEN_SUED
    }
}
