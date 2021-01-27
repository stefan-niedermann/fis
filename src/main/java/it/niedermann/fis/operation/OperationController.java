package it.niedermann.fis.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class OperationController {

    private static final Logger logger = LoggerFactory.getLogger(OperationController.class);

    private final OperationDispatcher dispatcher;

    public OperationController(OperationDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @GetMapping("/operation")
    public ResponseEntity<OperationDto> pollOperation() {
        final var operation = dispatcher.getCurrentOperation();
        if (operation == null) {
            logger.info("🚒 Operation got polled, but currently not active operation");
            return ResponseEntity.noContent().build();
        }
        logger.info("🚒 Operation got polled: " + operation.keyword);
        return ResponseEntity.ok(operation);
    }
}
