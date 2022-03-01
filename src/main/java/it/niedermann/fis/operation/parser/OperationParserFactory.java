package it.niedermann.fis.operation.parser;

import org.springframework.stereotype.Service;

@Service
class OperationParserFactory {

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public OperationParser createParser(OperationParserType type) {
        switch (type) {
            case MITTELFRANKEN_SUED:
                return new MittelfrankenSuedParser();
            default:
                throw new IllegalArgumentException("Could not find a " + OperationParser.class.getSimpleName() + " for type \"" + type + "\"");
        }
    }
}
