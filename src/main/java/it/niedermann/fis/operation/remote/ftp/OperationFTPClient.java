package it.niedermann.fis.operation.remote.ftp;

import it.niedermann.fis.FisConfiguration;
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
            FisConfiguration config
    ) throws IOException {
        connect(config.ftp().host());
        if (!login(config.ftp().username(), config.ftp().password())) {
            throw new IllegalArgumentException("🚒 Could not connect to FTP server + " + config.ftp().host() + ". Please check FTP credentials.");
        }
        logger.info("🚒 Connected to FTP server " + getRemoteAddress() + " on port " + getRemotePort() + ", polling each " + config.ftp().pollInterval() / 1_000 + " seconds.");
    }
}
