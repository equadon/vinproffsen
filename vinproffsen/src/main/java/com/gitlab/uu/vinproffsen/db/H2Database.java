package com.gitlab.uu.vinproffsen.db;

import com.gitlab.uu.vinproffsen.exceptions.WineDatabaseException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.impl.DSL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * H2 database base class.
 *
 * @author Niklas Persson
 * @version 2016-03-17
 */
public abstract class H2Database {
    private final static Logger LOG = Logger.getLogger(H2Database.class.getName());

    private static final String DB_DRIVER = "org.h2.Driver";

    private final String uri;

    protected Connection connection;

    /**
     * Construct a H2 database from a JDBC URI.
     */
    H2Database(String uri) {
        this.uri = uri;
    }

    /**
     * Check if connected.
     * @return true if connected
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Get JOOQ DSL.
     * @return JOOQ DSL
     */
    public DSLContext getDSL() {
        return DSL.using(connection, SQLDialect.H2);
    }

    public SelectQuery getSelectQuery() {
        return getDSL().selectQuery();
    }

    public PreparedStatement getPreparedStatement(String query) {
        try {
            return connection.prepareStatement(query);
        } catch (SQLException e) {
            LOG.warning(e.getMessage());
            return null;
        }
    }

    /**
     * Connect to database.
     * @throws WineDatabaseException when failed to connect
     */
    public void connect() throws WineDatabaseException {
        connection = null;

        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new WineDatabaseException(e.getMessage());
        }

        try {
            connection = DriverManager.getConnection(uri, "", "");
        } catch (SQLException e) {
            throw new WineDatabaseException(e.getMessage());
        }
    }

    /**
     * Disconnect from database.
     * @throws WineDatabaseException
     */
    public void disconnect() throws WineDatabaseException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new WineDatabaseException(e.getMessage());
        }
    }

    /**
     * Create tables from file.
     * @param dropTables drop all tables first if true
     * @throws WineDatabaseException
     */
    public void createTables(boolean dropTables) throws WineDatabaseException {
        try {
            Statement statement = connection.createStatement();

            if (dropTables) {
                statement.execute("DROP TABLE IF EXISTS ITEM");
            }

            statement.execute(readFile("create_tables.sql"));
            statement.close();

            connection.commit();
        } catch (SQLException | IOException e) {
            throw new WineDatabaseException(e.getMessage());
        }
    }

    /**
     * Execute query with values.
     * @param query sql query
     * @param values values
     * @return result set
     * @throws WineDatabaseException
     */
    public ResultSet execute(String query, String... values) throws WineDatabaseException {
        if (!isConnected()) return null;

        try {
            PreparedStatement statement = connection.prepareStatement(query);

            for (int i = 0; i < values.length; i++)
                statement.setObject(i + 1, values[i]);

            return statement.executeQuery();
        } catch (SQLException e) {
            throw new WineDatabaseException(e.getMessage());
        }
    }

    /**
     * Execute a raw SQL query.
     * @param query sql query
     * @return result set
     * @throws WineDatabaseException
     */
    public ResultSet execute(String query) throws WineDatabaseException {
        if (!isConnected()) return null;

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = null;
            boolean result = statement.execute(query);

            if (result) {
                resultSet = statement.getResultSet();
            } else {
                LOG.warning("Failed to executed query: " + query);
            }
            return resultSet;
        } catch (SQLException e) {
            throw new WineDatabaseException(e.getMessage());
        }
    }

    public int getCount(String countQuery) {
        try {
            Statement statement = connection.createStatement();

            if (statement.execute(countQuery)) {
                ResultSet result = statement.getResultSet();
                if (result.next()) {
                    LOG.fine("Executed count query: " + countQuery);
                    return result.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Error counting items for query: " + countQuery, e);
        }

        return 0;
    }

    /**
     * Read SQL file.
     * @param resourceName resource name
     * @return filename as a string
     * @throws IOException
     */
    private String readFile(String resourceName) throws IOException {
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(H2Database.class.getClassLoader().getResourceAsStream(resourceName)));
        StringBuilder sb = new StringBuilder();

        while ((line = in.readLine()) != null) {
            sb.append(line).append('\n');
        }

        return sb.toString();
    }
}
