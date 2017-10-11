package com.gitlab.uu.vinproffsen;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.vinproffsen.exceptions.WineDatabaseException;
import com.gitlab.uu.vinproffsen.ui.views.*;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Wine application where everything is initialized.
 *
 * @author Niklas Persson
 * @version 2016-03-10
 */
public class WineApplication extends Application {
    private final static Logger LOG = Logger.getLogger(WineApplication.class.getName());

    public WineApplication() {
        super(null, true);
    }

    @Override
    protected void initialize() {
        WineSettings settings = WineSettings.getInstance();

        setApplicationMode();

        // Set title
        frame.setTitle(settings.getString("app.name") + " - " + (settings.isCustomer() ? "Kund" : "Personal"));

        // Read logging config file
        try {
            InputStream is = WineApplication.class.getClassLoader().getResourceAsStream("logging.properties");
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException | NullPointerException e) {
            LOG.warning("Unable to locate: logging.properties");
        }
        
        try {
            WineModel model = new WineModel(this, settings.getBoolean("db.memory"));

            getEvents().send("wine:load");

            // Show progress dialog
            ProgressView progressView = new ProgressView(this, model, frame);
            if (!model.isLoaded()) {
                progressView.show();
            }

            if (settings.isStaff()) {
                MenuView menuView = new MenuView(this, model);
                frame.setJMenuBar(menuView.render());
            }

            WineTableView wineView = new WineTableView(this, model);
            StartScreenView startScreenView = new StartScreenView(this, model);
            SavedListView savedListView = new SavedListView(this, model);
            AddWineView addWineView = new AddWineView(this, model);

            MainView mainView = new MainView(this, model);

            frame.getContentPane().add(mainView.render(wineView, startScreenView, savedListView, addWineView));
        } catch (WineDatabaseException e) {
            LOG.severe(e.getMessage());
        }

        LOG.info("Application initialized.");
    }

    private void setApplicationMode() {
        WineSettings settings = WineSettings.getInstance();

        String mode = settings.getString("app.mode");

        if (mode.equals("customer")) {
            settings.setCustomer(true);
        } else if (mode.equals("staff")) {
            settings.setCustomer(false);
        } else {
            int modeSelection = JOptionPane.showOptionDialog(frame, "Vill du köra programmet som kund eller personal?", "Välj Applikationsläge",
                                                             JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                                             new String[]{"Kund", "Personal"}, "Kund");
            if (modeSelection == JOptionPane.YES_OPTION) {
                settings.setCustomer(true);
            } else if (modeSelection == JOptionPane.NO_OPTION) {
                settings.setCustomer(false);
            } else {
                LOG.severe("User didn't choose application mode (customer or staff), quit the application.");
                System.exit(1);
            }
        }
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new WineApplication());
    }
}
