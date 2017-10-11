package com.gitlab.uu.mvp;

import javax.swing.*;

/**
 * Application base class.
 *
 * It creates a new visible JFrame and takes care of event aggregation.
 *
 * @author Niklas Persson
 * @version 2016-01-29
 */
public abstract class Application {
    private final EventAggregator eventAggregator = new EventAggregator();

    protected final JFrame frame;

    /**
     * Construct a new application with a specified title.
     * @param title window title
     */
    public Application(String title) {
        this(title, false);
    }

    /**
     * Construct a new application with a specified title.
     * @param title window title
     * @param centered true to center window on screen
     */
    public Application(String title, boolean centered) {
        frame = new JFrame(title);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        initialize();

        frame.pack();
        frame.setVisible(true);

        if (centered) {
            frame.setLocationRelativeTo(null);
        }
    }

    /**
     * Get the event aggregator.
     * @return EventAggregator object.
     */
    public EventAggregator getEvents() {
        return eventAggregator;
    }

    /**
     * Initialize the application.
     */
    protected abstract void initialize();
}
