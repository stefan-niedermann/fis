package it.niedermann.fis;

import it.niedermann.fis.parser.OperationFaxParser;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;

public class OperationWatcherThread extends Thread {

    private final static Logger logger = LoggerFactory.getLogger(OperationWatcherThread.class);

    public OperationWatcherThread(String ftpUrl, String ftpUsername, String ftpPassword, String ftpPath,
                                  String ftpSuffix, int ftpPollInterval, String tessdataPath, String tessLang, Consumer<OperationInformationDto> operationListener) {
        super(() -> {
            final OperationsUtil operationsUtil = new OperationsUtil(tessdataPath, tessLang);
            final FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.connect(ftpUrl);
                if (!ftpClient.login(ftpUsername, ftpPassword)) {
                    throw new IllegalArgumentException("Could not connect to FTP server + " + ftpUrl + ". Please check FTP credentials.");
                }
                logger.info("🚒 Connected to FTP server " + ftpUrl + ", palling each " + ftpPollInterval / 1000 +  " seconds.");

                final OperationFaxParser parser = OperationFaxParser.create("mittelfranken-sued");

                String lastPdfName = "";

                //noinspection InfiniteLoopStatement
                while (true) {
                    logger.debug("Checking FTP server for incoming operations");
                    try {
                        final String finalLastPdfName = lastPdfName;
                        final Optional<FTPFile> ftpFileOptional = Arrays.stream(ftpClient.listFiles(ftpPath))
                                .filter(FTPFile::isFile)
                                .filter(file -> file.getName().endsWith(ftpSuffix))
                                .sorted(Comparator
                                        .comparingLong(file -> ((FTPFile) file).getTimestamp().getTimeInMillis())
                                        .reversed())
                                .limit(1)
                                .filter(file -> !finalLastPdfName.equals(file.getName()))
                                .findFirst();
                        if (ftpFileOptional.isPresent()) {
                            final FTPFile ftpFile = ftpFileOptional.get();

                            lastPdfName = ftpFile.getName();
                            logger.debug("Found a new file: \"" + ftpFile.getName() + "\"");

                            if ("".equals(finalLastPdfName)) {
                                logger.debug("Skipping first recognized file after startup");
                                continue;
                            }

                            logger.info("🚒 New incoming PDF detected: " + ftpFile.getName());

                            final File localFile = File.createTempFile("operation-", ".pdf");
                            logger.debug("→ Created temporary file: " + localFile.getName());

                            final OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
                            final boolean success = ftpClient.retrieveFile(ftpPath + "/" + ftpFile.getName(), outputStream);
                            outputStream.close();

                            if (success) {
                                logger.info("🚒 → Downloaded file to: " + localFile.getName());
                                final String ocrText = operationsUtil.fromImage(localFile);
                                final OperationInformationDto dto = parser.parse(ocrText);
                                operationListener.accept(dto);
                                logger.info("🚒 → Successfully extracted text from PDF file.");
                                if (!localFile.delete()) {
                                    logger.warn("🚒 → Could not delete downloaded FTP file!");
                                }
                            } else {
                                logger.warn("🚒 → Could not download new FTP file!");
                            }
                        } else {
                            logger.debug("→ No new file with suffix \"" + ftpSuffix + "\" is present at the server.");
                        }
                        //noinspection BusyWait
                        sleep(ftpPollInterval);
                    } catch (TesseractException e) {
                        logger.error("🚒 → Could not parse", e);
                    } catch (InterruptedException e) {
                        logger.error(
                                "🚒 → " + OperationWatcherThread.class.getSimpleName() + " has been interrupted.", e);
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }, OperationWatcherThread.class.getSimpleName());
    }
}
