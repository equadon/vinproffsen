package com.gitlab.uu.vinproffsen.ui.presenters;

import com.gitlab.uu.email.Email;
import com.gitlab.uu.email.EmailException;
import com.gitlab.uu.email.SMTPMailServer;
import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.WineSettings;
import com.gitlab.uu.vinproffsen.db.WineResult;
import com.gitlab.uu.vinproffsen.emails.WineListEmail;
import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.ui.views.SavedListView;

import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Presenter that takes care of the user's saved wine list.
 *
 * @author Niklas Persson
 * @version 2016-03-15
 */
public class SavedListPresenter extends TablePresenter<WineModel, SavedListView> {
    private static final Logger LOG = Logger.getLogger(SavedListPresenter.class.getName());

    public SavedListPresenter(Application application, SavedListView view, WineModel model) {
        super(application, view, model);

        listen("wine:list:add", (wines) -> view.addWines((List<Wine>) wines));
        listen("wine:list:remove", (wines) -> view.removeWines((List<Wine>) wines));
    }

    @Override
    public void tableHeaderClicked(MouseEvent e) {}

    @Override
    public void tableClicked(MouseEvent e) {}

    @Override
    public void pageChanged(int page) {}

    public void sendEmail(String address, List<Wine> wines) {
        WineSettings settings = WineSettings.getInstance();

        // remove null wines (which is used when no wine was found)
        wines.removeAll(Collections.singleton(null));

        String fromEmail = "valekhz@gmail.com";

        String hostname = settings.getString("smtp.hostname", null);
        int port = settings.getInteger("smtp.port");
        String username = settings.getString("smtp.username", null);
        String password = settings.getString("smtp.password", null);

        if (wines.size() == 0) {
            view.emailFailed("inga viner i vinlistan");
            return;
        }

        if (hostname == null || username == null || password == null) {
            view.emailFailed("Ingen mailserver konfigurerad.");
            return;
        }

        // Start a new thread when sending email so we don't freeze up the GUI
        new Thread(() -> {
            SMTPMailServer server = new SMTPMailServer(hostname, port, username, password, true);

            Email email = null;
            try {
                email = new WineListEmail("Din vinlista - VinProffsen - " + Math.round(1000 * Math.random()), address, wines);

                if (email.send(server, fromEmail)) {
                    view.emailSent(address);
                } else {
                    view.emailFailed("Kunde inte skicka din vinlista till: " + address);
                }
            } catch (EmailException e) {
                LOG.log(Level.SEVERE, "Unable to send email", e);

                view.emailFailed(e.getMessage());
            }
        }).start();
    }
}
