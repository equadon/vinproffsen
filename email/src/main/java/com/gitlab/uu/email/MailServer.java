package com.gitlab.uu.email;

import javax.mail.Session;

/**
 * Email server base class.
 *
 * @author Niklas Persson
 * @version 2016-02-07
 */
public abstract class MailServer {
    private final String username;
    private final String password;
    private final String host;
    private final int port;

    /**
     * Constructor.
     * @param username server username
     * @param password server password
     * @param host server hostname
     * @param port server port
     */
    public MailServer(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * Getter for server username.
     * @return server username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for server password.
     * @return server password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Getter for server hostname.
     * @return server hostname.
     */
    public String getHostname() {
        return host;
    }

    /**
     * Getter for server port.
     * @return server port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Get session object using the server information.
     * @return Session object.
     */
    public abstract Session getSession();
}
