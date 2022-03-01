package it.niedermann.fis.operation.parser;

import org.springframework.stereotype.Service;

@Service
class OperationParserFactory {

    @SuppressWarnings({"SwitchStatementWithTooFewBranches", "UnnecessaryDefault"})
    public OperationParser createParser(OperationParserType type) {
        return switch (type) {
            case MITTELFRANKEN_SUED -> new MittelfrankenSuedParser();
            default -> throw new IllegalArgumentException("Could not find a " + OperationParser.class.getSimpleName() + " for type \"" + type + "\"");
        };
    }
}
