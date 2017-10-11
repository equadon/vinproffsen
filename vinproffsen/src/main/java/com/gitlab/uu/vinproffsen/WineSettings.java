package com.gitlab.uu.vinproffsen;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Settings for the application as a singleton.
 *
 * @author Niklas Persson
 */
public class WineSettings {
    private static final Logger LOG = Logger.getLogger(WineSettings.class.getName());

    private static final String filename = "./vinproffsen.properties";
    private static WineSettings instance;

    private Properties properties;

    private Boolean customer;

    private WineSettings(String filename) {
        InputStream input = null;
        try {
            properties = new Properties();
            input = new FileInputStream("vinproffsen.properties");

            properties.load(input);
        } catch (IOException e) {
            System.out.println("Unable to load properties file: " + filename);
            System.exit(1);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String dbFilename = getString("db.file");
        if (!(dbFilename.startsWith(".") || dbFilename.startsWith("/"))) {
            System.out.println("Error: Database filename needs to start with a dot (.) or a slash (/).");
            System.exit(1);
        }

        customer = null;
    }

    public void setCustomer(boolean customer) {
        if (this.customer == null) {
            this.customer = customer;

            LOG.fine("Set mode to: " + (customer ? "Customer" : "Staff"));
        } else {
            LOG.warning("Failed to set application mode to " + (customer ? "customer" : "staff") + " because it was already set.");
        }
    }

    /**
     * Helper method to determine if the application is in customer or staff mode.
     * @return true if in customer mode
     */
    public Boolean isCustomer() {
        return customer;
    }

    /**
     * Helper method to determine if the application is in customer or staff mode.
     * @return true if in staff mode
     */
    public boolean isStaff() {
        return !isCustomer();
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public String getString(String key, String defaultValue) {
        String value = getString(key);

        if (value == null || value.isEmpty()) {
            return defaultValue;
        }

        return value;
    }

    public int getInteger(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(key);
    }

    public static WineSettings getInstance() {
        if (instance == null) {
            instance = new WineSettings(filename);
        }
        return instance;
    }
}
