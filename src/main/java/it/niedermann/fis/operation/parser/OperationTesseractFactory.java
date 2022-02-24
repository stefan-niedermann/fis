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
        if (ObjectUtils.isEmpty(config.getTesseract().getTessdata())) {
            config.getTesseract().setTessdata(extractTessResources("tessdata").getAbsolutePath());
        }
        final var tesseract = new Tesseract();
        tesseract.setVariable("LC_ALL", "C");
        tesseract.setDatapath(config.getTesseract().getTessdata());
        tesseract.setLanguage(config.getTesseract().getLang());
        return tesseract;
    }
}
