package it.niedermann.fis.operation.remote;

import it.niedermann.fis.FisConfiguration;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
class OperationFTPClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(OperationFTPClientFactory.class);

    public FTPClient createFTPClient(FisConfiguration config) throws IOException {
        final var ftpClient = new FTPClient();
        ftpClient.connect(config.getFtp().getHost());
        if (!ftpClient.login(config.getFtp().getUsername(), config.getFtp().getPassword())) {
            throw new IllegalArgumentException("🚒 Could not connect to FTP server + " + config.getFtp().getHost() + ". Please check FTP credentials.");
        }
        logger.info("🚒 Connected to FTP server " + config.getFtp().getHost() + ", palling each " + config.getFtp().getPollInterval() / 1_000 + " seconds.");
        return ftpClient;
    }
}
