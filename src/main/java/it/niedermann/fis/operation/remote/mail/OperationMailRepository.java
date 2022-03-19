package it.niedermann.fis.operation.remote.mail;

import it.niedermann.fis.FisConfiguration;
import it.niedermann.fis.main.model.OperationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class OperationMailRepository {

    private final Logger logger = LoggerFactory.getLogger(OperationMailRepository.class);

    private final Optional<JavaMailSender> mailSender;
    private final Optional<String> sender;
    private final Collection<String> recipients;

    public OperationMailRepository(
            FisConfiguration config,
            Optional<JavaMailSender> mailSender
    ) {
        this.mailSender = mailSender;
        this.sender = Optional.ofNullable(config.operation().sender());
        this.recipients = config.operation().recipients() == null
                ? Collections.emptyList()
                : config.operation().recipients();

        if (mailSender.isPresent()) {
            this.logger.info("✅ SMTP configuration is present");

            if (recipients.size() > 0) {
                this.logger.info("✅ " + recipients.size() + " recipients configured");

                if (sender.isPresent()) {
                    this.logger.info("✅ Sender configured: " + sender);
                } else {
                    this.logger.warn("No Sender is configured: Mails might go to junk folder");
                }
            } else {
                if (sender.isPresent()) {
                    this.logger.info("No recipients are configured: No mail notification will be sent");
                } else {
                    this.logger.info("No recipients and no sender are configured: No mail notification will be sent");
                }
            }
        } else {
            if (recipients.size() > 0) {
                this.logger.info("✅ " + recipients.size() + " recipients configured");

                if (sender.isPresent()) {
                    this.logger.info("✅ Sender configured: " + sender);
                } else {
                    this.logger.warn(recipients.size() + " recipients are configured, but SMTP configuration is missing.");
                }
            } else {
                if (sender.isPresent()) {
                    this.logger.info("✅ Sender configured: " + sender);
                } else {
                    this.logger.warn("No Sender is configured: Mails might go to junk folder");
                }
            }
        }
    }

    @Async
    public void send(OperationDto operation) {
        mailSender.ifPresentOrElse(
                mailSender -> {
                    if (this.recipients.size() > 0) {
                        final var messages = this.recipients
                                .stream()
                                .map(recipient -> createMessage(recipient, sender.orElse("JarFIS"), operation))
                                .toArray(SimpleMailMessage[]::new);
                        try {
                            mailSender.send(messages);
                            logger.info("✉ Successfully sent mail to " + recipients.size() + " recipients");
                        } catch (MailException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                },
                () -> this.logger.trace("✉️ Skipped sending mails because SMTP has not been configured.")
        );
    }

    private SimpleMailMessage createMessage(String recipient, String sender, OperationDto operation) {
        final var message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipient);
        message.setSubject(operation.getKeyword());
        message.setText(operation.getLocation());
        return message;
    }
}
