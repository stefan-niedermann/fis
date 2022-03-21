package it.niedermann.fis.operation.remote.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
class OperationFTPClient extends FTPClient {

    /** @noinspection FieldCanBeLocal*/
    private final Logger logger = LoggerFactory.getLogger(OperationFTPClient.class);

    public OperationFTPClient(
            FtpConfiguration config
    ) throws IOException {
        connect(config.host());
        if (!login(config.username(), config.password())) {
            throw new IllegalArgumentException("❌ Could not connect to FTP server + " + config.host() + ". Please check FTP credentials.");
        }
        logger.info("✅ Connected to FTP server " + getRemoteAddress() + " on port " + getRemotePort() + ", polling each " + config.pollInterval() / 1_000 + " seconds.");
    }
}
