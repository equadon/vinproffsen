package com.gitlab.uu.vinproffsen;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.Model;
import com.gitlab.uu.vinproffsen.db.H2WineDatabase;
import com.gitlab.uu.vinproffsen.db.H2WineFileDatabase;
import com.gitlab.uu.vinproffsen.db.H2WineMemoryDatabase;
import com.gitlab.uu.vinproffsen.db.WineResult;
import com.gitlab.uu.vinproffsen.exceptions.WineDatabaseException;
import com.gitlab.uu.vinproffsen.items.Beverage;
import com.gitlab.uu.vinproffsen.items.ItemFactory;
import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.items.storage.*;
import com.gitlab.uu.vinproffsen.ui.table.WineTable;
import com.gitlab.uu.vinproffsen.ui.table.WineTableColumn;
import com.gitlab.uu.vinproffsen.ui.views.WineTableView;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SelectQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

/**
 * Wine model provides an easier interface to the H2 database.
 *
 * @author Niklas Persson
 * @version 2016-03-14
 */
public class WineModel extends Model {
    private final static Logger LOG = Logger.getLogger(WineModel.class.getName());

    private final H2WineDatabase db;
    private boolean loaded;

    public WineModel(Application application, boolean memoryDatabase) throws WineDatabaseException {
        super(application);

        WineSettings settings = WineSettings.getInstance();

        // Create database
        if (memoryDatabase) {
            db = new H2WineMemoryDatabase("vinproffsen");
        } else {
            db = new H2WineFileDatabase(settings.getString("db.file"));
        }

        db.connect();
        db.createTables(false);

        listen("wine:load", (o) -> load());
    }

    /**
     * Check if the database is fully loaded.
     * @return true if loaded
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Called when items were loaded from source.
     */
    public void finished() {
        listen("wine:db:search", tableView -> searchFromView((WineTableView) tableView));

        send("wine:update:charts");
        send("wine:update:random");
        send("wine:progress", 100, 100);

        loaded = true;
    }

    public ResultSet execute(String query) throws WineDatabaseException {
        return db.execute(query);
    }

    /**
     * Get the total number of wines in the db.
     * @return number of wines
     */
    public int getWineCount() {
        String query = db.getDSL().selectCount()
                                  .from("ITEMS").toString();

        try {
            ResultSet result = db.execute(query);

            if (result.next())
                return result.getInt(1);
        } catch (SQLException | WineDatabaseException e) {
            LOG.warning(e.getMessage());
        }

        return 0;
    }

    /**
     * Get a wine from an ID.
     * @param id wine ID
     * @return wine
     */
    public WineResult getWine(Integer id) {
        String query = db.getDSL().select().from("ITEMS").where("ID = ?", id).limit(1).toString();

        return getWine(query, WineTableColumn.Id, true);
    }

    /**
     * Get a random wine.
     * @return random wine
     */
    public WineResult getRandomWine() {
        String query = "SELECT * FROM ITEMS ORDER BY RAND() LIMIT 1";

        return getWine(query, WineTableColumn.Id, true);
    }

    /**
     * Get a list of all wines.
     * @return list of all wines
     */
    public WineResult getWines() {
        return getWines(WineTableColumn.FullName, true);
    }

    public WineResult getWines(WineTableColumn sortBy, boolean ascending) {
        SelectQuery query = db.getSelectQuery();
        query.addFrom(table("ITEMS"));

        updateSortBy(query, sortBy, ascending);

        return getWines(query.toString(), sortBy, ascending, 0, 0);
    }

    /**
     * Called when the view wants an updated search.
     * @param view wine table view tab
     */
    private void searchFromView(WineTableView view) {
        WineTable table = view.getTable();

        String query = view.getSearchText();
        String type = view.getSelectedType().id;
        Double minPrice = view.getMinPrice();
        Double maxPrice = view.getMaxPrice();
        Integer minYear = view.getMinYear();
        Integer maxYear = view.getMaxYear();
        ItemDetails details = new ItemDetails(view.getEcological(), view.getKosher(), null, null);
        ItemSeller seller = new ItemSeller(null, view.getSelectedArea().id, view.getSelectedCountry().id, null, null, null, null);
        Beverage beverage = null;

        int page = table.getCurrentPage();
        int winesPerPage = table.getWinesPerPage();
        WineTableColumn sortBy = table.getSortedBy();
        boolean ascending = table.isAscending();

        int offset = (page - 1) * winesPerPage;

        new Thread(() -> {
            WineResult result = searchWines(query, type, minPrice, maxPrice, minYear, maxYear, details, seller, beverage,
                                            sortBy, ascending, offset, winesPerPage);

            send("wine:db:result", result);
        }).start();
    }

    /**
     * Search the database for all wines matching the provided parameters. Set to null to skip matching a certain param.
     * @param searchQuery text query that matches wines by name, type, country and area
     * @param type wine type
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param minYear minimum year
     * @param maxYear maximum year
     * @param details item details
     * @param seller item seller
     * @param beverage beverage details
     * @param sortBy column to sort by
     * @param ascending ascending or descending order
     * @return all wines matching the provided parameters
     */
    public WineResult searchWines(String searchQuery, String type, Double minPrice, Double maxPrice, Integer minYear, Integer maxYear,
                                  ItemDetails details, ItemSeller seller, Beverage beverage, WineTableColumn sortBy, boolean ascending,
                                  int offset, int winesPerPage) {
        if (searchQuery == null)
            searchQuery = "";

        if (WineSettings.getInstance().getBoolean("app.debug")) {
            // DEBUG: if search query is an integer assume it's a wine id and return that wine
            try {
                int wineId = Integer.parseInt(searchQuery);

                LOG.fine("Retrieved item with ID: " + wineId);

                return getWine(wineId);
            } catch (NumberFormatException ignored) {}

            // DEBUG: if query starts with "select * from items" assume it's a raw sql query
            if (searchQuery.toLowerCase().startsWith("select * from items")) {
                LOG.fine("Executing raw query: " + searchQuery);

                return getWines(searchQuery, sortBy, ascending, offset, winesPerPage);
            }
        }

        SelectQuery query = db.getSelectQuery();
        query.addFrom(table("ITEMS"));

        // Wine details
        if (details != null) {
            if (details.ecological)
                query.addConditions(field("ecological").eq(1));
            if (details.kosher)
                query.addConditions(field("kosher").eq(1));
        }

        // Search by text query
        handleSearchQuery(query, searchQuery);

        // Search by selected country, area, and/or type
        if (type != null) {
            query.addConditions(field("type_lower").contains(type.toLowerCase()));
        }

        if (seller != null) {
            if (seller.area != null)
                query.addConditions(field("area_lower").eq(seller.area.toLowerCase()));
            if (seller.country != null)
                query.addConditions(field("country_lower").eq(seller.country.toLowerCase()));
        }

        // Filter by year
        filterBetweenValues(query, "year", minYear, maxYear);

        // Filter by price
        minPrice = (minPrice != null) ? minPrice * 100d : null;
        maxPrice = (maxPrice != null) ? maxPrice * 100d : null;
        filterBetweenValues(query, "price", minPrice, maxPrice);

        if (winesPerPage > 0) {
            // add limit and offset
            query.addLimit(offset, winesPerPage);
        }

        // Sorting
        updateSortBy(query, sortBy, ascending);

        LOG.finer(query.toString());

        return getWines(query.toString(), sortBy, ascending, offset, winesPerPage);
    }

    /**
     * Remove wines from database.
     * @param wines wine to be removed
     */
    public void remove(List<Wine> wines) {
        db.remove(wines);
    }

    /**
     * Get a list of countries.
     * @return list of countries as strings
     */
    public List<String> getCountries() {
        String query = db.getDSL().selectDistinct(field("COUNTRY")).from("ITEMS").orderBy(field("COUNTRY").asc()).toString();

        List<String> countries = new ArrayList<>();

        try {
            ResultSet result = db.execute(query);
            while (result.next())
                countries.add(result.getString("COUNTRY"));
        } catch (SQLException | WineDatabaseException ignored) {}

        return countries;
    }

    /**
     * Get a list of areas.
     * @return list of areas as strings
     */
    public List<String> getAreas() {
        String query = db.getDSL().selectDistinct(field("AREA")).from("ITEMS").orderBy(field("AREA").asc()).toString();

        List<String> areas = new ArrayList<>();

        try {
            ResultSet result = db.execute(query);
            while (result.next())
                areas.add(result.getString("AREA"));
        } catch (SQLException | WineDatabaseException ignored) {}

        return areas;
    }

    /**
     * Get a full list of wine types. Some types are initially comma separated but those are split into separate types.
     * @return list of wine types as strings
     */
    public List<String> getWineTypes() {
        String query = db.getDSL().selectDistinct(field("TYPE")).from("ITEMS").toString();

        List<String> types = new ArrayList<>();

        try {
            ResultSet result = db.execute(query);
            while (result.next()) {
                for (String type : result.getString("TYPE").split(",")) {
                    type = type.trim();
                    if (!types.contains(type))
                        types.add(type);
                }
            }
        } catch (SQLException | WineDatabaseException ignored) {}

        return types;
    }

    /**
     * Add wine to database.
     * @param name wine name/alternate name
     * @param type item type
     * @param seller item seller
     * @param details item details
     * @param bevDetails beverage details
     * @return WineResult object that holds the newly created wine object
     */
    public WineResult addWine(ItemName name, ItemType type, ItemSeller seller, ItemDetails details, BeverageDetails bevDetails) {
        int id = db.addWine(name, type, seller, details, bevDetails);

        WineResult result = getWine(id);

        // notify consumers about the new wine
        send("wine:update:charts");
        send("wine:update:table");
        send("wine:added", result);

        return result;
    }

    /**
     * Helper method that return a Wine object from an SQL query.
     * @param query database query
     * @return wine object
     */
    private WineResult getWine(String query, WineTableColumn sortedBy, boolean ascending) {
        long startTime = System.nanoTime();

        Wine wine = null;
        try {
            ResultSet result = db.execute(query);
            if (result.next())
                wine = ItemFactory.createWine(result);
        } catch (SQLException | WineDatabaseException e) {
            LOG.warning(e.getMessage());
        }

        long endTime = System.nanoTime();
        double executionTime = (double) (endTime - startTime) / 1000000d;

        send("wine:status:right", String.format("Sökningen tog %.1f millisekunder.", executionTime));

        return new WineResult(wine, sortedBy, ascending, executionTime);
    }

    /**
     * Helper method that return a list of Wine objects from an SQL query.
     * @param query database query
     * @return wine object
     */
    private WineResult getWines(String query, WineTableColumn sortedBy, boolean ascending, int offset, int winesPerPage) {
        long startTime = System.nanoTime();

        int count = 0;
        List<Wine> wines = new ArrayList<>();

        try {
            ResultSet result = db.execute(query);
            while (result.next()) {
                Wine wine = ItemFactory.createWine(result);
                if (wine != null) {
                    wines.add(wine);
                    count++;
                }
            }
        } catch (SQLException | WineDatabaseException e) {
            LOG.warning(e.getMessage());
        }

        int page = 1;
        int totalPages = (int) Math.ceil(count / WineSettings.getInstance().getInteger("db.winesPerPage"));
        int totalCount = count;
        if (winesPerPage > 0) {
            page = 1 + offset / winesPerPage;

            // No need to look up wine count if offset is at zero and we get less than winesPerPage wines
            if (!(offset == 0 && count < winesPerPage)) {
                String countQuery = query.toLowerCase().replace("select *", "select count(*)").replaceFirst("order by [^ ]+ (asc|desc)", "").replaceFirst(" offset [^ ]+", "");
                totalCount = db.getCount(countQuery);
                totalPages = 1 + totalCount / winesPerPage;
            } else {
                totalPages = 1;
            }
        }

        long endTime = System.nanoTime();
        double executionTime = (double) (endTime - startTime) / 1000000d;

        // Update random wine
        send("wine:update:random");

        // Update status bar
        send("wine:status:right", String.format("Sökningen tog %.1f millisekunder.", executionTime));

        return new WineResult(wines, sortedBy, ascending, executionTime, page, totalPages, winesPerPage, totalCount);
    }

    /**
     * Apply conditions to SQL query to filter between two numbers.
     * @param query select query
     * @param field database field
     * @param min min value
     * @param max max value
     */
    private void filterBetweenValues(SelectQuery query, String field, Number min, Number max) {
        if (min != null && max != null) {
            if (max.doubleValue() >= min.doubleValue())
                query.addConditions(field(field).between(min, max));
            else
                query.addConditions(field(field).greaterOrEqual(min));
        } else if (min != null) {
            query.addConditions(field(field).greaterOrEqual(min));
        } else if (max != null) {
            query.addConditions(field(field).lessOrEqual(max));
        }
    }

    /**
     * Perform a fuzzy search for names, types, countries and areas.
     * @param query select query
     * @param search user input query
     */
    private void handleSearchQuery(SelectQuery query, String search) {
        String[] keywords = search.toLowerCase().split(" ");

        if (search != null && !search.isEmpty() && keywords != null && keywords.length > 0) {
            String[] searchBy = new String[] { "name_lower", "name2_lower", "type_lower", "area_lower", "country_lower", "year"/*, "producer_lower", "supplier_lower"*/ };

            Condition condition = field("name_lower").contains(keywords[0]);
            for (int j = 1; j < searchBy.length; j++)
                condition = condition.or(field(searchBy[j]).contains(keywords[0]));
            query.addConditions(condition);

            for (int i = 1; i < keywords.length; i++) {
                condition = field("name_lower").contains(keywords[i]);
                for (int j = 1; j < searchBy.length; j++)
                    condition = condition.or(field(searchBy[j]).contains(keywords[i]));
                query.addConditions(condition);
            }
        }
    }

    /**
     * Apply ordering to query.
     * @param query select query
     * @param sortBy column to sort by
     * @param ascending ascending or descending order
     */
    private void updateSortBy(SelectQuery query, WineTableColumn sortBy, boolean ascending) {
        Field sortCol;
        if (sortBy == WineTableColumn.Id)
            sortCol = field("id");
        else if (sortBy == WineTableColumn.FullName)
            sortCol = field("name");
        else if (sortBy == WineTableColumn.Type)
            sortCol = field("type");
        else if (sortBy == WineTableColumn.Price)
            sortCol = field("price");
        else if (sortBy == WineTableColumn.Volume)
            sortCol = field("volume");
        else if (sortBy == WineTableColumn.Year)
            sortCol = field("year");
        else if (sortBy == WineTableColumn.From)
            sortCol = field("from_full");
        else if (sortBy == WineTableColumn.Ecological)
            sortCol = field("ecological");
        else if (sortBy == WineTableColumn.Kosher)
            sortCol = field("kosher");
        else if (sortBy == WineTableColumn.Alcohol)
            sortCol = field("alcohol");
        else
            sortCol = field("id");

        if (ascending)
            query.addOrderBy(sortCol.asc());
        else
            query.addOrderBy(sortCol.desc());
    }

    /**
     * Called when application has requested the model to load wines from XML file.
     */
    private void load() {
        // Load items
        if (getWineCount() == 0)
            new Thread(() -> new WineReader(this, "sb-2016-02-28.xml")).start();
        else
            finished();
    }
}
