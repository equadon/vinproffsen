package com.gitlab.uu.email;

import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.Properties;

/**
 * Send emails using the SMTP protocol.
 *
 * @author Niklas Persson
 * @version 2016-02-07
 */
public class SMTPMailServer extends MailServer {
    private boolean secure;

    /**
     * Concstruct a new SMTP server.
     * @param username smtp username
     * @param password smtp password
     * @param hostname smtp hostname
     * @param port smtp port
     * @param secure true to use tls
     */
    public SMTPMailServer(String hostname, int port, String username, String password, boolean secure) {
        super(hostname, port, username, password);

        this.secure = secure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");

        if (secure)
            props.put("mail.smtp.starttls.enable", "true");

        props.put("mail.smtp.host", getHostname());
        props.put("mail.smtp.port", getPort());

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(getUsername(), getPassword());
            }
        });

        return session;
    }
}
