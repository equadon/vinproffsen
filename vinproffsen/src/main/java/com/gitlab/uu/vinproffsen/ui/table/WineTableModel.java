package com.gitlab.uu.vinproffsen.ui.table;

import com.gitlab.uu.mvp.View;
import com.gitlab.uu.vinproffsen.Utility;
import com.gitlab.uu.vinproffsen.WineSettings;
import com.gitlab.uu.vinproffsen.db.WineResult;
import com.gitlab.uu.vinproffsen.items.Wine;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model that manages the wine table contents.
 *
 * @author Niklas Persson
 * @version 2016-03-23
 */
public class WineTableModel extends AbstractTableModel {
    public static final WineTableColumn[] COLUMNS = new WineTableColumn[] {
                WineTableColumn.FullName,
                WineTableColumn.Type,
                WineTableColumn.Price,
                WineTableColumn.Volume,
                WineTableColumn.Year,
                WineTableColumn.From,
                WineTableColumn.Ecological,
                WineTableColumn.Kosher,
                WineTableColumn.Alcohol
    };
    private final View view;

    private List<Wine> wines;
    private WineTableColumn sortedBy;
    private boolean ascending;

    private int winesPerPage;
    private int currentPage;
    private int totalPages;
    private int totalCount;

    public WineTableModel(View view) {
        this.view = view;

        wines = new ArrayList<>();
        wines.add(null);

        sortedBy = WineTableColumn.Id;
        ascending = true;

        winesPerPage = WineSettings.getInstance().getInteger("db.winesPerPage");
        currentPage = 1;
        totalPages = 1;
    }

    public WineTableColumn getSortedBy() {
        return sortedBy;
    }

    public void setSortedBy(WineTableColumn column) {
        sortedBy = column;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public int getWinesPerPage() {
        return winesPerPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Set current page to page.
     * @param page new page
     * @return true if page was changed
     */
    public boolean setPage(int page) {
        if (page > 0 && page <= totalPages) {
            currentPage = page;
            return true;
        }

        return false;
    }

    @Override
    public int getRowCount() {
        int count = 0;

        for (Wine wine : wines)
            if (wine != null)
                count++;

        return count;
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int col) {
        switch (COLUMNS[col]) {
            case Id:
                return "ID";
            case FullName:
                return "Namn";
            case Type:
                return "Typ";
            case Price:
                return "Pris";
            case Volume:
                return "Volym";
            case Year:
                return "Årgång";
            case From:
                return "Från";
            case Ecological:
                return "Ekologisk";
            case Kosher:
                return "Kosher";
            case Alcohol:
                return "Alkoholhalt";
            default:
                return COLUMNS[col].toString();
        }
    }

    /**
     * Get wine by row id.
     * @param row row id
     * @return wine object
     */
    public Wine getWine(int row) {
        return wines.get(row);
    }

    @Override
    public String getValueAt(int row, int col) {
        if (wines.size() == 1 && wines.get(0) == null) {
            WineTableColumn column = COLUMNS[col];

            if (column == WineTableColumn.FullName)
                return "Hittade inga viner.";
            else
                return "";
        }

        Wine wine = wines.get(row);

        if (wine != null) {
            switch (COLUMNS[col]) {
                case Id:
                    return Integer.toString(wine.getId());
                case FullName:
                    return wine.getFullName().toString();
                case Type:
                    return wine.getType().text;
                case Price:
                    return Math.round((double) wine.getPrice() / 100d) + ":-";
                case Volume:
                    return wine.getVolume() + " ml";
                case Year:
                    return wine.getYear() == 0 ? "-" : Integer.toString(wine.getYear());
                case From:
                    return wine.getFrom();
                case Ecological:
                    return wine.isEcological() ? "Ja" : "-";
                case Kosher:
                    return wine.isKosher() ? "Ja" : "-";
                case Alcohol:
                    return String.format("%.1f%%", wine.getAlcohol() * 100);
            }
        }

        return "???";
    }

    /**
     * Update wines in model.
     * @param result wines
     */
    public void setWines(WineResult result) {
        wines = result.wines;

        if (wines.isEmpty()) {
            wines.add(null);
        }

        this.sortedBy = result.sortedBy;
        this.ascending = result.ascending;
        this.currentPage = result.currentPage;
        this.winesPerPage = result.winesPerPage;
        this.totalPages = result.totalPages;
        this.totalCount = result.totalCount;

        view.send("wine:status:left", Utility.pluralize(result.totalCount, "vin", "viner") + " matchade din sökning.");

        fireTableDataChanged();
    }
}
