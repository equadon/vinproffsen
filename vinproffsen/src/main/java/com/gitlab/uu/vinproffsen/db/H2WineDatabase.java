package com.gitlab.uu.vinproffsen.db;

import com.gitlab.uu.vinproffsen.items.Beverage;
import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.items.storage.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * H2 database with helpers to create/remove wines.
 *
 * @author Niklas Persson
 * @version 2016-03-24
 */
public abstract class H2WineDatabase extends H2Database {
    private final static Logger LOG = Logger.getLogger(H2WineDatabase.class.getName());

    /**
     * Construct a H2 database from a JDBC URI.
     *
     * @param uri JDBC URI
     */
    H2WineDatabase(String uri) {
        super(uri);
    }

    /**
     * Add wine to database given a set of parameters.
     * @return id for the created wine, null if failed
     */
    public Integer addWine(ItemName name, ItemType type, ItemSeller seller, ItemDetails details, BeverageDetails bevDetails) {
        PreparedStatement insert = getPreparedStatement("INSERT INTO ITEMS" + "(name, name2, type, sell_start, area, country, producer, supplier, year, price, ecological, kosher, assortment, description, volume, price_per_liter, deposit, packaging, seal, alcohol, from_full) values" + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        Integer id = null;

        if (insert != null) {
            try {
                addWine(insert, name, type, seller, details, bevDetails);

                ResultSet keyResult = insert.getGeneratedKeys();

                if (keyResult.next()) {
                    id = keyResult.getInt(1);
                }

                insert.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return id;
    }

    public void addWine(PreparedStatement insert, ItemName name, ItemType type, ItemSeller seller, ItemDetails details, BeverageDetails bevDetails) throws SQLException {
        insert.setString(1, name.name);
        insert.setString(2, name.alternativeName);
        insert.setString(3, type.text);
        insert.setString(4, seller.sellStart);

        insert.setInt(10, seller.price);
        insert.setString(14, details.description);

        insert.setString(5, seller.area);
        insert.setString(6, seller.country);
        insert.setString(7, seller.producer);
        insert.setString(8, seller.supplier);
        insert.setInt(9, seller.year);
        insert.setBoolean(11, details.ecological);
        insert.setBoolean(12, details.kosher);
        insert.setString(13, details.assortment);
        insert.setDouble(15, bevDetails.volume);
        insert.setObject(16, bevDetails.pricePerLiter);
        insert.setObject(17, bevDetails.deposit);
        insert.setString(18, bevDetails.packaging);
        insert.setString(19, bevDetails.seal);
        insert.setDouble(20, bevDetails.alcohol);
        insert.setString(21, Beverage.getFullFrom(seller.country, seller.area));

        insert.executeUpdate();
    }

    /**
     * Add many wines in same transaction.
     * TODO: Remove this and use the methods addWines() above.
     * @param wines list of wine objects to add to database
     */
    public void addWines(List<Wine> wines) {
        if (wines.isEmpty()) return;

        PreparedStatement insert = null;
        String query = "INSERT INTO ITEMS" + "(id, name, name2, type, sell_start, area, country, producer, supplier, year, price, ecological, kosher, assortment, description, volume, price_per_liter, deposit, packaging, seal, alcohol, from_full) values" + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            connection.setAutoCommit(false);

            insert = connection.prepareStatement(query);

            for (Wine wine : wines) {
                insert.setInt(1, wine.getId());
                insert.setString(2, wine.getName());
                insert.setString(3, wine.getAlternativeName());
                insert.setString(4, wine.getType().text);
                insert.setString(5, wine.getSellStart());

                insert.setInt(11, wine.getPrice());
                insert.setString(15, wine.getDescription());

                insert.setString(6, wine.getArea());
                insert.setString(7, wine.getCountry());
                insert.setString(8, wine.getProducer());
                insert.setString(9, wine.getSupplier());
                insert.setInt(10, wine.getYear());
                insert.setBoolean(12, wine.isEcological());
                insert.setBoolean(13, wine.isKosher());
                insert.setString(14, wine.getAssortment());
                insert.setDouble(16, wine.getVolume());
                insert.setInt(17, wine.getPricePerLiter());
                insert.setInt(18, wine.getDeposit());
                insert.setString(19, wine.getPackaging());
                insert.setString(20, wine.getSeal());
                insert.setDouble(21, wine.getAlcohol());
                insert.setString(22, wine.getFrom());

                insert.executeUpdate();
            }

            insert.close();

            connection.commit();
        } catch (SQLException e) {
            LOG.warning(e.getMessage());
        }
    }

    /**
     * Remove wines from database.
     * @param wines list of wines to remove
     */
    public void remove(List<Wine> wines) {
        if (wines.isEmpty()) return;

        String query = "DELETE FROM ITEMS WHERE ID = ?";

        try {
            connection.setAutoCommit(false);

            PreparedStatement delete = connection.prepareStatement(query);

            for (Wine wine : wines) {
                delete.setInt(1, wine.getId());
                delete.executeUpdate();
            }

            delete.close();

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
