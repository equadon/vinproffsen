package com.gitlab.uu.vinproffsen.db;

import java.util.logging.Logger;

/**
 * H2 file database.
 *
 * @author Niklas Persson
 * @version 2016-03-14
 */
public class H2WineFileDatabase extends H2WineDatabase {
    private final static Logger LOG = Logger.getLogger(H2WineFileDatabase.class.getName());

    /**
     * Construct a file-based H2 database.
     *
     * @param filename filename for the database
     */
    public H2WineFileDatabase(String filename) {
        super("jdbc:h2:file:" + filename);

        LOG.fine("Initiating H2 file database: " + filename);
    }
}
