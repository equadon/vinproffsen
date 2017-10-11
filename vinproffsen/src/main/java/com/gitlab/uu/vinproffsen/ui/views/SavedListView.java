package com.gitlab.uu.vinproffsen.ui.views;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.View;
import com.gitlab.uu.vinproffsen.Utility;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.db.WineResult;
import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.ui.presenters.SavedListPresenter;
import com.gitlab.uu.vinproffsen.ui.table.WineTable;
import com.gitlab.uu.vinproffsen.ui.table.WineTableColumn;
import com.gitlab.uu.vinproffsen.ui.table.WineTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * View that renders the user saved wine list.
 *
 * @author Niklas Persson
 * @version 2016-03-12
 */
public class SavedListView extends View<WineModel, SavedListPresenter> {
    private final static Logger LOG = Logger.getLogger(SavedListView.class.getName());

    private final List<Wine> savedList;

    private final WineTableModel dataModel;
    private final WineTable wineTable;
    private final JLabel listCount;

    public SavedListView(Application application, WineModel model) {
        super(application, model);

        savedList = new ArrayList<>();

        dataModel = new WineTableModel(presenter.getView());

        wineTable = new WineTable(presenter);

        listCount = new JLabel("Viner: 0");
    }

    public JPanel render() {
        JLabel title = new JLabel("Din vinlista");
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 28));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JButton sendButton = new JButton(new AbstractAction("Skicka vinlistan med email") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sendEmail();
            }
        });

        JPanel topPanel = new JPanel();
        topPanel.add(sendButton);
        topPanel.add(listCount);

        JPanel center = new JPanel(new BorderLayout());
        center.add(topPanel, BorderLayout.NORTH);
        center.add(new JScrollPane(wineTable), BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(title, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    private void sendEmail() {
        String email = JOptionPane.showInputDialog(null, "Din emailaddress:");

        if (email != null && !email.isEmpty()) {
            presenter.sendEmail(email, savedList);
        }
    }

    public void addWines(List<Wine> wines) {
        int count = 0;

        for (Wine wine : wines) {
            if (!savedList.contains(wine)) {
                savedList.add(wine);
                count++;
            }
        }

        if (!wines.isEmpty()) {
            LOG.info("Vinlistan uppdaterad med nya viner: " + count + " st.");

            JOptionPane.showMessageDialog(null, "Du har lagt till " + Utility.pluralize(count, "nytt vin", "nya viner") + " i din vinlista.");
        }

        updateModel();
    }

    public void removeWines(List<Wine> wines) {
        for (Wine wine : wines)
            if (savedList.contains(wine))
                savedList.remove(wine);

        LOG.info("Removed " + wines.size() + " wine(s).");

        updateModel();
    }

    public void emailSent(String email) {
        JOptionPane.showMessageDialog(null, "Din vinlista har skickats till: " + email);
    }

    public void emailFailed(String error) {
        JOptionPane.showMessageDialog(null, "Kunde inte skicka email: " + error);
    }

    private void updateModel() {
        wineTable.setWines(new WineResult(savedList, WineTableColumn.FullName, true, 0));

        listCount.setText("Viner: " + savedList.size());
    }

    public List<Wine> getSelectedWines() {
        return wineTable.getSelectedWines();
    }

    @Override
    protected SavedListPresenter createPresenter(WineModel model) {
        return new SavedListPresenter(application, this, model);
    }
}
