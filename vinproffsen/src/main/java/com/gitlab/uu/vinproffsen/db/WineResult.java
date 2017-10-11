package com.gitlab.uu.vinproffsen.db;

import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.ui.table.WineTableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that holds a wine query result from the database.
 */
public class WineResult {
    // Wines
    public final List<Wine> wines;
    public final int count;
    public final int totalCount;

    // Sorting
    public final WineTableColumn sortedBy;
    public final boolean ascending;

    public final double executionTime; // milliseconds

    // Pagination
    public final int currentPage;
    public final int totalPages;
    public final int winesPerPage;

    public WineResult() {
        this(new ArrayList<>(), WineTableColumn.Id, true, 0);
    }

    public WineResult(Wine wine, WineTableColumn sortedBy, boolean ascending, double executionTime) {
        this(asList(wine), sortedBy, ascending, executionTime);
    }

    public WineResult(List<Wine> wines, WineTableColumn sortedBy, boolean ascending, double executionTime) {
        this(wines, sortedBy, ascending, executionTime, 1, 1, 1, 1);
    }

    public WineResult(List<Wine> wines, WineTableColumn sortedBy, boolean ascending, double executionTime, int currentPage, int totalPages, int winesPerPage, int totalCount) {
        this.wines = wines;
        this.count = wines.size();
        this.totalCount = totalCount;

        this.sortedBy = sortedBy;
        this.ascending = ascending;

        this.executionTime = executionTime;

        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.winesPerPage = winesPerPage;
    }

    private static List<Wine> asList(Wine... wines) {
        List<Wine> wineList = new ArrayList<>();
        for (Wine wine : wines)
        wineList.add(wine);

        return wineList;
    }
}
