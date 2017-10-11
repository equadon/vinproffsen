package com.gitlab.uu.vinproffsen.ui.presenters;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.ui.ComboBoxItem;
import com.gitlab.uu.vinproffsen.ui.WineDialog;
import com.gitlab.uu.vinproffsen.ui.table.WineTableColumn;
import com.gitlab.uu.vinproffsen.ui.table.WineTableModel;
import com.gitlab.uu.vinproffsen.ui.views.WineTableView;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.logging.Logger;

/**
 * Presenter that takes care of the wine table searching etc.
 *
 * @author Niklas Persson
 * @version 2016-03-14
 */
public class WineTablePresenter extends TablePresenter<WineModel, WineTableView> {
    private static final Logger LOG = Logger.getLogger(WineTablePresenter.class.getName());

    public WineTablePresenter(Application application, WineTableView view, WineModel model) {
        super(application, view, model);

        listen("wine:update:table", o -> searchWines(true));

        listen("wine:remove", wines -> {
            model.remove((java.util.List<Wine>) wines);
            searchWines(true);
        });
    }

    /**
     * Search wines by using the data from the view.
     * @return result of wine search
     */
    public void searchWines(boolean resetPage) {
        view.setTableLoading(true);

        if (resetPage)
            view.getTable().setPage(1);

        send("wine:db:search", view);
    }

    /**
     * User clicked table header.
     */
    @Override
    public void tableHeaderClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            JTableHeader tableHeader = (JTableHeader) e.getSource();
            JTable table = tableHeader.getTable();

            WineTableModel tableModel = (WineTableModel) table.getModel();

            int col = table.columnAtPoint(e.getPoint());
            WineTableColumn column = WineTableModel.COLUMNS[col];

            boolean ascending = (tableModel.getSortedBy() != column) || !tableModel.isAscending();

            LOG.info("Sorting by column '" + column + "' in " + (ascending ? "ascending" : "descending") + " order.");

            view.getTable().setSortedBy(column);
            view.getTable().setAscending(ascending);

            searchWines(false);
        }
    }

    /**
     * User clicked table.
     */
    @Override
    public void tableClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
            JTable table = (JTable) e.getSource();
            WineTableModel tableModel = (WineTableModel) table.getModel();

            int row = table.rowAtPoint(e.getPoint());
            Wine wine = tableModel.getWine(row);

            new WineDialog(wine, view);
        }
    }

    @Override
    public void pageChanged(int page) {
        searchWines(false);
    }

    /**
     * Create combobox model for countries.
     */
    public DefaultComboBoxModel<ComboBoxItem> getCountries() {
        DefaultComboBoxModel<ComboBoxItem> items = new DefaultComboBoxModel<>();

        items.addElement(new ComboBoxItem(null, "Länder"));
        for (String country : model.getCountries())
            items.addElement(new ComboBoxItem(country, country));

        return items;
    }

    /**
     * Create combobox model for areas.
     */
    public DefaultComboBoxModel<ComboBoxItem> getAreas() {
        DefaultComboBoxModel<ComboBoxItem> items = new DefaultComboBoxModel<>();

        items.addElement(new ComboBoxItem(null, "Områden"));
        for (String area : model.getAreas())
            if (!area.isEmpty())
                items.addElement(new ComboBoxItem(area, area));

        return items;
    }

    /**
     * Create combobox model for wine types.
     */
    public DefaultComboBoxModel<ComboBoxItem> getWineTypes() {
        List<String> rawTypes = model.getWineTypes();
        List<String> types = new ArrayList<>();

        Set<String> ignore = new HashSet<>();
        ignore.add("Mousserande vin");
        ignore.add("Rött vin");
        ignore.add("Rött");
        ignore.add("Vitt  halvtorrt");
        ignore.add("Vitt sött");
        ignore.add("Vitt torrt");
        ignore.add("Vitt vin");
        ignore.add("Vitt");
        ignore.add("Rosévin");
        ignore.add("Rosé");

        types.add("Torrt");
        types.add("Halvtorrt");

        for (String type : rawTypes) {
            if (ignore.contains(type)) continue;

            if (!types.contains(type))
                types.add(type);
        }

        Collections.sort(types);

        types.add(0, "Rött vin");
        types.add(1, "Vitt vin");
        types.add(2, "Mousserande");
        types.add(3, "Rosé");

        DefaultComboBoxModel<ComboBoxItem> items = new DefaultComboBoxModel<>();

        items.addElement(new ComboBoxItem(null, "Vintyper"));
        for (String type : types)
            items.addElement(new ComboBoxItem(type, type));

        return items;
    }
}
