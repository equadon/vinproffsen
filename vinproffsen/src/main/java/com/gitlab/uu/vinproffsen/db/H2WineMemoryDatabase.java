package com.gitlab.uu.vinproffsen.db;

import java.util.logging.Logger;

/**
 * H2 memory database.
 *
 * @author Niklas Persson
 * @version 2016-03-14
 */
public class H2WineMemoryDatabase extends H2WineDatabase {
    private final static Logger LOG = Logger.getLogger(H2WineMemoryDatabase.class.getName());

    /**
     * Construct a memory-based H2 database give a JDBC URI.
     *
     * @param name name of database
     */
    public H2WineMemoryDatabase(String name) {
        super("jdbc:h2:mem:" + name);

        LOG.fine("Initiating H2 memory database.");
    }
}
