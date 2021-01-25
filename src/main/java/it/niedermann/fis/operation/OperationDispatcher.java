package it.niedermann.fis.operation;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.operation.parser.OperationFaxParser;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import static net.sourceforge.tess4j.util.LoadLibs.extractTessResources;

@Service
public class OperationDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(OperationDispatcher.class);

    private final SimpMessagingTemplate template;

    private final ITesseract tesseract;
    private final FTPClient ftpClient;
    private final OperationFaxParser parser;

    private final String ftpPath;
    private final String ftpFileSuffix;

    private String lastPdfName = "";

    public OperationDispatcher(
            SimpMessagingTemplate template,
            FisConfiguration config) throws IOException {
        this.template = template;
        this.ftpPath = config.getFtp().getPath();
        this.ftpFileSuffix = config.getFtp().getFileSuffix();

        if (ObjectUtils.isEmpty(config.getTesseract().getTessdata())) {
            config.getTesseract().setTessdata(extractTessResources("tessdata").getAbsolutePath());
        }
        tesseract = new Tesseract();
        tesseract.setTessVariable("LC_ALL", "C");
        tesseract.setDatapath(config.getTesseract().getTessdata());
        tesseract.setLanguage(config.getTesseract().getLang());

        ftpClient = new FTPClient();
        ftpClient.connect(config.getFtp().getHost());
        if (!ftpClient.login(config.getFtp().getUsername(), config.getFtp().getPassword())) {
            throw new IllegalArgumentException("🚒 Could not connect to FTP server + " + config.getFtp().getHost() + ". Please check FTP credentials.");
        }
        logger.info("🚒 Connected to FTP server " + config.getFtp().getHost() + ", palling each " + config.getFtp().getPollInterval() / 1000 + " seconds.");

        parser = OperationFaxParser.create("mittelfranken-sued");
    }

    @Scheduled(fixedDelayString = "${fis.ftp.pollInterval}")
    public void pollOperations() {
        try {
            final var finalLastPdfName = lastPdfName;
            final var optionalFtpFile = poll(finalLastPdfName);
            if (optionalFtpFile.isPresent()) {
                final FTPFile ftpFile = optionalFtpFile.get();

                lastPdfName = ftpFile.getName();
                logger.debug("Found a new file: \"" + ftpFile.getName() + "\"");

                if ("".equals(finalLastPdfName)) {
                    logger.debug("Skipping first recognized file after startup");
                    return;
                }

                downloadAndProcessFTPFile(ftpFile);
            } else {
                logger.debug("→ No new file with suffix \"" + ftpFileSuffix + "\" is present at the server.");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void downloadAndProcessFTPFile(FTPFile ftpFile) throws IOException {
        logger.info("🚒 New incoming PDF detected: " + ftpFile.getName());

        final var localFile = File.createTempFile("operation-", ".pdf");
        logger.debug("→ Created temporary file: " + localFile.getName());

        try (final var outputStream = new BufferedOutputStream(new FileOutputStream(localFile))) {
            if (ftpClient.retrieveFile(ftpPath + "/" + ftpFile.getName(), outputStream)) {
                outputStream.close();

                logger.info("🚒 → Start OCR for \"" + localFile.getName() + "\"…");
                final var ocrText = tesseract.doOCR(localFile);
                logger.debug("→ Finished OCR");

                logger.debug("→ Start parsing with " + parser.getClass().getSimpleName() + "…");
                final var dto = parser.parse(ocrText);
                logger.debug("→ Finished parsing");

                logger.debug("→ Start Broadcasting operation: \"" + dto.keyword + "\"…");
                template.convertAndSend("/notification/operation", dto);
                logger.info("🚒 → Finished broadcast for \"" + dto.keyword + "\".");

            } else {
                logger.warn("🚒 → Could not download new FTP file!");
            }
        } catch (TesseractException e) {
            logger.error("🚒 → Could not parse", e);
        } catch (IllegalArgumentException e) {
            logger.info("🚒 → The given file could not be validated as an operation fax.");
        } finally {
            if (!localFile.delete()) {
                logger.warn("🚒 → Could not delete downloaded FTP file: " + localFile.getName());
            }
        }
    }

    private Optional<FTPFile> poll(String excludeFileName) throws IOException {
        logger.debug("Checking FTP server for incoming operations");
        return Arrays.stream(ftpClient.listFiles(ftpPath))
                .filter(FTPFile::isFile)
                .filter(file -> file.getName().endsWith(ftpFileSuffix))
                .sorted(Comparator
                        .<FTPFile>comparingLong(file -> file.getTimestamp().getTimeInMillis())
                        .reversed())
                .limit(1)
                .filter(file -> !Objects.equals(excludeFileName, file.getName()))
                .findFirst();
    }
}
