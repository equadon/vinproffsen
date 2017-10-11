package com.gitlab.uu.vinproffsen.items;

import com.gitlab.uu.vinproffsen.items.storage.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Create items in different ways.
 *
 * @author Niklas Persson
 * @version 2016-03-16
 */
public class ItemFactory {
    private static final Logger LOG = Logger.getLogger(ItemFactory.class.getName());

    /**
     * Create an item from the first result of an SQL query.
     * @param res ResultSet with data
     * @return Item
     */
    public static Wine createWine(ResultSet res) {
        try {
            int id = res.getInt("ID");

            ItemName name = new ItemName(res.getString("NAME"), res.getString("NAME2"));

            String typeText = res.getString("TYPE");
            ItemType type = new ItemType(typeText);

            String sellStart = res.getString("SELL_START");
            String area = res.getString("AREA");
            String country = res.getString("COUNTRY");
            String producer = res.getString("PRODUCER");
            String supplier = res.getString("SUPPLIER");

            int year = res.getInt("YEAR");
            int price = res.getInt("PRICE");

            ItemSeller seller = new ItemSeller(sellStart, area, country, producer, supplier, year, price);

            boolean ecological = res.getBoolean("ECOLOGICAL");
            boolean kosher = res.getBoolean("KOSHER");
            String assortment = res.getString("ASSORTMENT");
            String description = res.getString("DESCRIPTION");

            ItemDetails details = new ItemDetails(ecological, kosher, assortment, description);

            int volume = res.getInt("VOLUME");
            int pricePerLiter = res.getInt("PRICE");
            int deposit = res.getInt("DEPOSIT");
            String packaging = res.getString("ASSORTMENT");
            String seal = res.getString("DESCRIPTION");
            double alcohol = res.getDouble("ALCOHOL");

            BeverageDetails bevDetails = new BeverageDetails(volume, pricePerLiter, deposit, packaging, seal, alcohol);

            return new Wine(id, name, type, seller, details, bevDetails);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
