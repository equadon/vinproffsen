package com.gitlab.uu.vinproffsen.ui.presenters;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.Presenter;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.exceptions.WineDatabaseException;
import com.gitlab.uu.vinproffsen.items.Item;
import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.ui.views.StartScreenView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter that takes care of the start screen logic.
 *
 * @author Niklas Persson
 * @version 2016-03-15
 */
public class StartScreenPresenter extends Presenter<WineModel, StartScreenView> {
    public StartScreenPresenter(Application application, StartScreenView view, WineModel model) {
        super(application, view, model);

        listen("wine:update:charts", (o) -> updateCharts());
        listen("wine:update:random", (o) -> randomizeWine());
    }

    /**
     * Update the random wine shown on the start screen.
     */
    private void randomizeWine() {
        view.updateRandomWine(model.getRandomWine());
    }

    /**
     * Update chart data.
     */
    private void updateCharts() {
        view.getChartsPanel().removeAll();

        countAllWines();
        countEcoWines();
    }

    /**
     * Count wines and sort them by country and type.
     */
    private void countAllWines() {
        Map<String, Map<String, Integer>> wineCount = new LinkedHashMap<>();

        for (String country : new String[] {"Frankrike", "Spanien", "Italien", "Övriga"}) {
            if (!wineCount.containsKey(country)) {
                Map<String, Integer> typeMap = new HashMap<>();
                typeMap.put("Rött", 0);
                typeMap.put("Vitt", 0);
                typeMap.put("Mousserande", 0);
                typeMap.put("Rosé", 0);

                wineCount.put(country, typeMap);
            }
        }

        int count = 0;

        try {
            ResultSet resultSet = model.execute("select * from items");

            while (resultSet.next()) {
                String type = resultSet.getString("type");
                String country = resultSet.getString("country");

                if (!(country.contains("Frankrike") || country.contains("Italien") || country.contains("Spanien")))
                    country = "Övriga";

                Map<String, Integer> types = wineCount.get(country);

                if (type.toLowerCase().contains("rött"))
                    types.put("Rött", types.get("Rött") + 1);
                if (type.toLowerCase().contains("rosé"))
                    types.put("Rosé", types.get("Rosé") + 1);
                if (type.toLowerCase().contains("vitt"))
                    types.put("Vitt", types.get("Vitt") + 1);
                if (type.toLowerCase().contains("mousserande"))
                    types.put("Mousserande", types.get("Mousserande") + 1);

                count++;
            }
        } catch (SQLException | WineDatabaseException e) {
            return;
        }

        view.updateWineCountChart(count, wineCount);
    }

    /**
     * Count ecological wines.
     */
    private void countEcoWines() {
        int whiteCount = 0;
        int redCount = 0;
        int sparklingCount = 0;
        int roseCount = 0;
        int unknownCount = 0;

        try {
            ResultSet resultSet = model.execute("select * from items where ecological = 1");

            while (resultSet.next()) {
                String type = resultSet.getString("type");

                if (type.toLowerCase().contains("rosé")) {
                    roseCount++;
                } else if (type.toLowerCase().contains("mousserande")) {
                    sparklingCount++;
                } else if (type.toLowerCase().contains("rött")) {
                    redCount++;
                } else if (type.toLowerCase().contains("vitt")) {
                    whiteCount++;
                } else {
                    unknownCount++;
                }
            }

            // notify view
            view.updateEcoWineChart(redCount, whiteCount, sparklingCount, roseCount, unknownCount);
        } catch (SQLException | WineDatabaseException e) {
            e.printStackTrace();
        }
    }
}
