package it.niedermann.fis.operation.remote.notification.mail;

import it.niedermann.fis.main.model.OperationDto;
import it.niedermann.fis.operation.remote.notification.NotificationConfiguration;
import it.niedermann.fis.operation.remote.notification.OperationNotificationUtil;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class MailProvider implements Consumer<OperationDto> {

    private final Logger logger = LoggerFactory.getLogger(MailProvider.class);

    private final Optional<JavaMailSender> mailSender;
    private final OperationNotificationUtil notificationUtil;
    private final String sender;
    private final Optional<String> origin;
    private final Collection<String> recipients;

    public MailProvider(
            NotificationConfiguration config,
            OperationNotificationUtil notificationUtil,
            Optional<JavaMailSender> mailSender,
            Optional<String> origin
    ) {
        this.mailSender = mailSender;
        this.notificationUtil = notificationUtil;
        this.sender = config.senderName() + Optional
                .ofNullable(config.senderMail())
                .map(mail -> String.format(" <%s>", mail))
                .orElse("");
        this.origin = origin;
        this.recipients = filterMailRecipients(config.mail());

        if (mailSender.isPresent()) {
            logger.info("✅ Found SMTP configuration");
        }
    }

    @Override
    public void accept(OperationDto operation) {
        mailSender.ifPresentOrElse(
                mailSender -> {
                    if (this.recipients.size() > 0) {
                        final var messages = this.recipients
                                .stream()
                                .map(recipient -> createMessage(recipient, sender, origin, operation))
                                .toArray(SimpleMailMessage[]::new);
                        try {
                            mailSender.send(messages);
                            logger.info("✉ Successfully sent mail to " + recipients.size() + " notification");
                        } catch (MailException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                },
                () -> this.logger.trace("✉️ Skipped sending mails because SMTP has not been configured.")
        );
    }

    @SuppressWarnings("SpellCheckingInspection")
    private SimpleMailMessage createMessage(String recipient, String sender, Optional<String> origin, OperationDto operation) {
        final var message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipient);
        message.setSubject(String.format("%s, %s", operation.getKeyword(), String.join(", ", operation.getTags())));
        message.setText(String.format("Einsatz: %s, %s %s, %s".stripIndent(),
                operation.getKeyword(),
                operation.getStreet(),
                operation.getNumber(),
                operation.getLocation()
        ));
        message.setText(String.format("""
                            Schlagworte: %s (%s)
                            Adresse: %s
                            %s
                            
                            Notiz: %s
                            Alarmiert: %s
                        """.stripLeading(),
                operation.getKeyword(),
                String.join(", ", operation.getTags()),
                notificationUtil.getHumanReadableLocation(operation),
                origin.isPresent()
                        ? notificationUtil.getGoogleMapsLink(operation, origin.get())
                        : notificationUtil.getGoogleMapsLink(operation),
                operation.getNote(),
                String.join(", ", operation.getVehicles())));
        return message;
    }

    private Collection<String> filterMailRecipients(Collection<String> recipients) {
        final var validator = EmailValidator.getInstance();
        return recipients == null
                ? Collections.emptyList()
                : recipients.stream().filter(validator::isValid).toList();
    }
}
