package com.gitlab.uu.vinproffsen.ui;

import com.gitlab.uu.mvp.View;
import com.gitlab.uu.vinproffsen.WineSettings;
import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.ui.views.SavedListView;
import com.gitlab.uu.vinproffsen.ui.views.WineTableView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Popup menu when right clicking a wine in the table.
 *
 * @author Niklas Persson
 * @version 2016-03-20
 */
public class WineListPopup extends JPopupMenu {
    public WineListPopup(View view) {
        super();

        WineSettings settings = WineSettings.getInstance();

        if (view instanceof WineTableView) {
            WineTableView wineView = (WineTableView) view;

            if (settings.isCustomer()) {
                add(new JMenuItem(new AbstractAction("Lägg till i vinlistan") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        List<Wine> selectedWines = wineView.getSelectedWines();

                        view.send("wine:list:add", selectedWines);
                    }
                }));
            } else {
                add(new JMenuItem(new AbstractAction("Ta bort från databasen") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        List<Wine> selectedWines = wineView.getSelectedWines();

                        view.send("wine:remove", selectedWines);
                        view.send("wine:update:charts");
                    }
                }));
            }
        } else if (view instanceof SavedListView) {
            SavedListView savedListView = (SavedListView) view;

            if (settings.isCustomer()) {
                add(new JMenuItem(new AbstractAction("Ta bort från vinlistan") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        List<Wine> selectedWines = savedListView.getSelectedWines();

                        view.send("wine:list:remove", selectedWines);
                    }
                }));
            }
        }
    }
}
