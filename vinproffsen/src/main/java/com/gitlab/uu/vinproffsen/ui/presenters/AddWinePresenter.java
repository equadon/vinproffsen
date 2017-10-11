package com.gitlab.uu.vinproffsen.ui.presenters;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.Presenter;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.db.WineResult;
import com.gitlab.uu.vinproffsen.items.storage.*;
import com.gitlab.uu.vinproffsen.ui.views.AddWineView;

/**
 * Presenter that takes care of the logic of adding a new wine.
 *
 * @author Niklas Persson
 * @version 2016-03-14
 */
public class AddWinePresenter extends Presenter<WineModel, AddWineView> {
    public AddWinePresenter(Application application, AddWineView view, WineModel model) {
        super(application, view, model);

        listen("wine:added", result -> {
            WineResult res = (WineResult) result;
            view.succeeded(res);

            if (res.count > 0)
                send("wine:search", res.wines.get(0).getSearchQuery());
        });
    }

    /**
     * Save wine to database.
     */
    public void saveWine(String nameText, String typeText, String priceText, String volumeText, String yearText,
                         String countryText, String areaText, String alcoholText, boolean ecoCheck, boolean kosherCheck) {
        Integer yearNum = null;
        try {
            yearNum = Integer.parseInt(yearText);
        } catch (NumberFormatException ignored) {}

        boolean numberError = false;

        Integer priceNum = null;
        try {
            priceNum = (int) (Double.parseDouble(priceText) * 100d);
        } catch (NumberFormatException ignored) {
            numberError = true;
        }

        Integer volume = null;
        try {
            volume = Integer.parseInt(volumeText);
        } catch (NumberFormatException ignored) {
            numberError = true;
        }

        Double alcohol = null;
        try {
            alcohol = Double.parseDouble(alcoholText) / 100d;
        } catch (NumberFormatException ignored) {
            numberError = true;
        }

        // Required fields
        if (nameText == null || nameText.isEmpty() || typeText == null || typeText.isEmpty()) {
            view.failed("Namn, typ och pris måste anges.");

            return;
        }

        if (numberError) {
            view.failed("Pris, volym, årtal och alkoholhalt måste vara siffror.");

            return;
        }

        ItemName name = new ItemName(nameText, "");
        ItemType type = new ItemType(typeText);
        ItemSeller seller = new ItemSeller(null, areaText, countryText, null, null, yearNum, priceNum);
        ItemDetails details = new ItemDetails(ecoCheck, kosherCheck, null, null);
        BeverageDetails bevDetails = new BeverageDetails(volume, null, null, null, null, alcohol);

        WineResult result = model.addWine(name, type, seller, details, bevDetails);

        if (result == null || result.count == 0)
            view.failed("Kunde inte lägga till vinet!");
    }
}
