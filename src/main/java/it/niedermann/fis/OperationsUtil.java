package it.niedermann.fis;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class OperationsUtil {

    private final ITesseract tesseract = new Tesseract();

    public OperationsUtil(String tessdataPath, String lang) {
        tesseract.setTessVariable("LC_ALL", "C");
        tesseract.setDatapath(tessdataPath);
        tesseract.setLanguage(lang);
    }

    public String fromImage(File imageFile) throws TesseractException {
        return tesseract.doOCR(imageFile);
    }
}
