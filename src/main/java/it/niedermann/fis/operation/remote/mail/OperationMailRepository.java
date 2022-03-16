package it.niedermann.fis.operation.remote.mail;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

@Service
public class OperationMailRepository {

    private final Logger logger = LoggerFactory.getLogger(OperationMailRepository.class);

    private final Optional<JavaMailSender> mailSender;
    private final Collection<String> recipients = new LinkedList<>();

    public OperationMailRepository(
            FisConfiguration config,
            Optional<JavaMailSender> mailSender
    ) {
        this.mailSender = mailSender;
        this.recipients.addAll(config.operation().recipients());
    }

    public void send(OperationDto operation) {
        mailSender.ifPresentOrElse(
                mailSender ->
                        this.recipients.forEach(recipient -> {
                            final var message = new SimpleMailMessage();
                            message.setFrom("jarfis@feuerwehr-aurachhoehe.de");
                            message.setTo(recipient);
                            message.setSubject(operation.getKeyword());
                            message.setText(operation.getLocation());
                            try {
                                mailSender.send(message);
                                logger.debug("Sent mail to " + recipient);
                            } catch (MailException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }),
                () ->
                        this.logger.debug("Skipping mail because SMTP has not been configured.")
        );
    }
}
