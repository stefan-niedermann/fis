package it.niedermann.fis.operation.parser;

import net.sourceforge.tess4j.Tesseract;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;


import static net.sourceforge.tess4j.util.LoadLibs.extractTessResources;

import java.util.Optional;

@Service
@SuppressWarnings("SpellCheckingInspection")
@EnableConfigurationProperties(TesseractConfiguration.class)
class OperationTesseractFactory {

    public Tesseract createTesseract(TesseractConfiguration config) {
        final var tesseract = new Tesseract();
        tesseract.setVariable("LC_ALL", "C");
        tesseract.setVariable("user_defined_dpi", String.valueOf(config.dpi())); // https://stackoverflow.com/a/58296472
        tesseract.setDatapath(Optional.ofNullable(config.tessdata()).orElse(extractTessResources("tessdata").getAbsolutePath()));
        tesseract.setLanguage(config.lang());
        return tesseract;
    }
}
