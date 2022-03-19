package it.niedermann.fis.operation.parser;

import it.niedermann.fis.FisConfiguration;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static net.sourceforge.tess4j.util.LoadLibs.extractTessResources;

@Service
@SuppressWarnings("SpellCheckingInspection")
class OperationTesseractFactory {

    public Tesseract createTesseract(FisConfiguration config) {
        final var tesseract = new Tesseract();
        tesseract.setVariable("LC_ALL", "C");
        tesseract.setVariable("user_defined_dpi", "150"); // https://stackoverflow.com/a/58296472
        tesseract.setDatapath(Optional.ofNullable(config.tesseract().tessdata()).orElse(extractTessResources("tessdata").getAbsolutePath()));
        tesseract.setLanguage(config.tesseract().lang());
        return tesseract;
    }
}
