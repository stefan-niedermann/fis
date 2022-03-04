package it.niedermann.fis.operation.parser;

import it.niedermann.fis.FisConfiguration;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import static net.sourceforge.tess4j.util.LoadLibs.extractTessResources;

@Service
@SuppressWarnings("SpellCheckingInspection")
class OperationTesseractFactory {

    public Tesseract createTesseract(FisConfiguration config) {
        final var tessdata = ObjectUtils.isEmpty(config.tesseract().tessdata())
                ? extractTessResources("tessdata").getAbsolutePath()
                : config.tesseract().tessdata();
        final var tesseract = new Tesseract();
        tesseract.setVariable("LC_ALL", "C");
        tesseract.setVariable("user_defined_dpi", "300"); // https://stackoverflow.com/a/58296472
        tesseract.setDatapath(tessdata);
        tesseract.setLanguage(config.tesseract().lang());
        return tesseract;
    }
}
