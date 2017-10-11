package com.gitlab.uu.vinproffsen.items;

import com.gitlab.uu.vinproffsen.items.storage.*;

/**
 * Beverage.
 *
 * @author Niklas Persson
 * @version 2016-03-12
 */
public class Beverage extends Item {
    protected BeverageDetails beverage;

    public Beverage(int id, ItemName name, ItemType type, ItemSeller seller, ItemDetails details, BeverageDetails beverage) {
        super(id, name, type, seller, details);

        this.beverage = beverage;
    }

    public Integer getVolume() {
        return beverage.volume;
    }

    public Integer getPricePerLiter() {
        return beverage.pricePerLiter;
    }

    public Integer getDeposit() {
        return beverage.deposit;
    }

    public String getPackaging() {
        return beverage.packaging;
    }

    public String getSeal() {
        return beverage.seal;
    }

    public String getCountry() {
        return seller.country;
    }

    public String getArea() {
        return seller.area;
    }

    public String getProducer() {
        return seller.producer;
    }

    public String getSupplier() {
        return seller.supplier;
    }

    public Integer getYear() {
        return seller.year;
    }

    public boolean isEcological() {
        return details.ecological;
    }

    public boolean isKosher() {
        return details.kosher;
    }

    public String getAssortment() {
        return details.assortment;
    }

    public Double getAlcohol() {
        return beverage.alcohol;
    }

    public boolean hasAlcohol() {
        return beverage.alcohol > 0;
    }

    public String getFrom() {
        return getFullFrom(getCountry(), getArea());
    }

    public void updateInfo(int id, ItemName name, ItemType type, ItemSeller seller, ItemDetails details, BeverageDetails beverage) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.seller = seller;
        this.details = details;
        this.beverage = beverage;
    }

    public static String getFullFrom(String country, String area) {
        if (area.isEmpty()) {
            return country;
        } else if (country.isEmpty()) {
            return area;
        } else {
            return area + ", " + country;
        }
    }
}
