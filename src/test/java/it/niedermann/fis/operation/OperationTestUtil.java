package it.niedermann.fis.operation;

import org.apache.commons.net.ftp.FTPFile;

import java.time.Instant;
import java.time.ZoneId;
import java.util.GregorianCalendar;

public class OperationTestUtil {

    public static FTPFile createFTPFile(String name, Instant timestamp) {
        return createFTPFile(name, timestamp, 0);
    }

    public static FTPFile createFTPFile(String name, Instant timestamp, long size) {
        return createFTPObject(name, timestamp, FTPFile.FILE_TYPE, size);
    }

    public static FTPFile createFTPObject(String name, Instant timestamp, int type) {
        return createFTPObject(name, timestamp, type, 0);
    }

    public static FTPFile createFTPObject(String name, Instant timestamp, int type, long size) {
        final var ftpFile = new FTPFile();
        ftpFile.setName(name);
        ftpFile.setTimestamp(GregorianCalendar.from(timestamp.atZone(ZoneId.systemDefault())));
        ftpFile.setType(type);
        ftpFile.setSize(size);
        return ftpFile;
    }
}
