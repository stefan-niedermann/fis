package it.niedermann.fis.operation.parser;

import it.niedermann.fis.main.model.OperationDto;
import net.sourceforge.tess4j.ITesseract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

import static it.niedermann.fis.operation.parser.OperationParserType.MITTELFRANKEN_SUED;

@SuppressWarnings("SpellCheckingInspection")
@Service
public class OperationParserRepository {

    private final Logger logger = LoggerFactory.getLogger(OperationParserRepository.class);

    private final ITesseract tesseract;
    private final OperationParser parser;

    public OperationParserRepository(
            TesseractConfiguration config,
            OperationTesseractFactory tesseractFactory,
            OperationParserFactory parserFactory
    ) {
        tesseract = tesseractFactory.createTesseract(config);
        parser = parserFactory.createParser(MITTELFRANKEN_SUED);
    }

    public Optional<OperationDto> parse(File source) {
        logger.info("Start parsing operation \"" + source.getName() + "\"…");
        try {
            logger.debug("→ Start OCR for \"" + source.getName());
            final var ocrText = tesseract.doOCR(source);
            logger.debug("→ Finished OCR");

            logger.debug("→ Start parsing with " + parser.getClass().getSimpleName() + "…");
            final var dto = parser.parse(ocrText);
            logger.debug("→ Finished parsing");

            logger.info("Finished parsing operation \"" + dto.getKeyword() + " from \"" + source.getName() + "\"");
            return Optional.of(dto);
        } catch (IllegalArgumentException e) {
            logger.info("The given file could not be validated as an operation fax.");
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " while parsing", e);
        }
        return Optional.empty();
    }
}
