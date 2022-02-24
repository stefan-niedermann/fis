package it.niedermann.fis.operation.parser;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

import static it.niedermann.fis.operation.parser.OperationParserFactory.Parser.MITTELFRANKEN_SUED;

@SuppressWarnings("SpellCheckingInspection")
@Service
public class OperationParserRepository {

    private static final Logger logger = LoggerFactory.getLogger(OperationParserRepository.class);

    private final ITesseract tesseract;
    private final OperationFaxParser parser;

    public OperationParserRepository(
            FisConfiguration config,
            OperationTesseractFactory tesseractFactory,
            OperationParserFactory parserFactory
    ) {
        tesseract = tesseractFactory.createTesseract(config);
        parser = parserFactory.createParser(MITTELFRANKEN_SUED);
    }

    public Optional<OperationDto> parse(File source) {
        try {
            logger.info("🚒 → Start OCR for \"" + source.getName() + "\"…");
            final var ocrText = tesseract.doOCR(source);
            logger.debug("→ Finished OCR");

            logger.debug("→ Start parsing with " + parser.getClass().getSimpleName() + "…");
            final var dto = parser.parse(ocrText);
            logger.debug("→ Finished parsing");

            return Optional.of(dto);
        } catch (TesseractException e) {
            logger.error("🚒 → Could not parse", e);
        } catch (IllegalArgumentException e) {
            logger.info("🚒 → The given file could not be validated as an operation fax.");
        } finally {
            if (!source.delete()) {
                logger.warn("🚒 → Could not delete downloaded FTP file: " + source.getName());
            }
        }
        return Optional.empty();
    }
}
