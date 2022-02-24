package it.niedermann.fis.operation;

import org.apache.commons.net.ftp.FTPFile;

import java.time.Instant;
import java.time.ZoneId;
import java.util.GregorianCalendar;

public class OperationTestUtil {

    public static FTPFile createFTPFile(String name, Instant timestamp) {
        return createFTPObject(name, timestamp, FTPFile.FILE_TYPE);
    }

    public static FTPFile createFTPObject(String name, Instant timestamp, int type) {
        final var ftpFile = new FTPFile();
        ftpFile.setName(name);
        ftpFile.setTimestamp(GregorianCalendar.from(timestamp.atZone(ZoneId.systemDefault())));
        ftpFile.setType(type);
        return ftpFile;
    }
}
