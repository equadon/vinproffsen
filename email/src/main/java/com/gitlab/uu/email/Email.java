package com.gitlab.uu.email;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Email class.
 *
 * @author Niklas Persson
 * @version 2016-02-07
 */
public class Email {
    private static final Logger LOG = Logger.getLogger(Email.class.getName());

    private final List<String> to;
    private final List<String> cc;
    private final List<String> bcc;
    private final List<String> images;
    private final String subject;
    private final String type;

    private String message;

    /**
     * Constructor.
     * @param subject email subject
     * @param to send email to
     * @param message email message
     * @param type email message type
     */
    public Email(String subject, String to, String message, String type) throws EmailException {
        this(subject, to, null, null, message, type);
    }

    /**
     * Constructor with more options.
     * @param subject email subject
     * @param to send email to
     * @param cc send copy to
     * @param bcc send hidden copy to
     * @param message email message
     * @param type email message type
     * @throws EmailException if to, bcc, or cc email addresses are invalid
     */
    public Email(String subject, String to, String cc, String bcc, String message, String type) throws EmailException {
        // Make sure email addresses are valid
        for (String address : new String[] {to, cc, bcc}) {
            if (address != null) {
                if (!isEmailValid(address)) {
                    LOG.warning("Invalid email address: " + to);

                    throw new EmailException(address + " är ingen giltig emailadress");
                }
            }
        }

        this.subject = subject;
        this.type = type;
        this.to = Arrays.asList(to);
        this.cc = Arrays.asList(cc);
        this.bcc = Arrays.asList(bcc);
        images = new ArrayList<>();

        this.message = message;
    }

    /**
     * Get email message.
     * @return email message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Add a recipient.
     * @param recipient email
     */
    public void addRecipient(String recipient) {
        to.add(recipient);
    }

    /**
     * Add a copy recipient.
     * @param recipient email
     */
    public void addCC(String recipient) {
        cc.add(recipient);
    }

    /**
     * Add a secret copy recipient.
     * @param recipient email
     */
    public void addBCC(String recipient) {
        bcc.add(recipient);
    }

    /**
     * Attach image to email.
     * @param filename image filename
     */
    public void addImage(String filename) {
        images.add(filename);
    }

    /**
     * Send email.
     * @param server email server
     * @param from from email
     * @return true if successful.
     */
    public boolean send(MailServer server, String from) {
        Session session = server.getSession();

        try {
            Message msg = buildMessage(session, from);

            Transport.send(msg);

            LOG.fine("Sent email to: " + msg.getAllRecipients());
        } catch (MessagingException e) {
            LOG.log(Level.WARNING, "Failed to send email", e);

            return false;
        }

        return true;
    }

    /**
     * Build a message object.
     * @param session current session.
     * @param from from email.
     * @return Message object.
     * @throws MessagingException
     */
    private Message buildMessage(Session session, String from) throws MessagingException {
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(String.join(", ", this.to)));
        msg.setSubject(subject);

        MimeMultipart multipart = new MimeMultipart("related");

        MimeBodyPart body;
        DataSource fileSource;

        // Text
        body = new MimeBodyPart();
        body.setContent(getMessage(), type);
        multipart.addBodyPart(body);

        // Images
        for (String filename : images) {
            body = new MimeBodyPart();
            fileSource = new FileDataSource(filename);

            body.setDataHandler(new DataHandler(fileSource));
            body.setHeader("Content-ID", "<image>");

            multipart.addBodyPart(body);
        }

        msg.setContent(multipart);

        return msg;
    }

    public static <T, U> String fromTemplate(Class cls, String templateDirectory, String templateFilename, Map<T, U> root) throws EmailException {
        if (cls == null || templateDirectory == null || templateFilename == null)
            throw new EmailException("Resource directory can't be null.");

        try {
            // Configuration
            // TODO: Put in a singleton
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setClassForTemplateLoading(cls, templateDirectory);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            BeansWrapper beansWrapper = (BeansWrapper) ObjectWrapper.BEANS_WRAPPER;
            beansWrapper.setExposeFields(true);
            cfg.setObjectWrapper(beansWrapper);

            // Setup template
            Template template = cfg.getTemplate(templateFilename);

            Writer out = new StringWriter();
            template.process(root, out);

            return out.toString();
        } catch (IOException ioe) {
            throw new EmailException("Unable to load resource directory: " + ioe.getMessage());
        } catch (TemplateException te) {
            LOG.log(Level.WARNING, "Failed to parse Freemarker template", te);

            throw new EmailException("Error parsing Freemarker template: " + te.getMessage());
        }
    }

    /**
     * Check if an email address is valid.
     * @param emailAddress email address to check.
     * @return true if email is valid.
     */
    public static boolean isEmailValid(String emailAddress) {
        String pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        return Pattern.matches(pattern, emailAddress.trim());
    }
}
