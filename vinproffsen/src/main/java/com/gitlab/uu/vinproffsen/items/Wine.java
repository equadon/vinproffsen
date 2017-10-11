package com.gitlab.uu.vinproffsen.items;

import com.gitlab.uu.vinproffsen.items.storage.*;

/**
 * The wine class provides a few helpers to determine wine type, other than that it's just a beverage.
 *
 * @author Niklas Persson
 * @version 2016-03-12
 */
public class Wine extends Beverage {
    public Wine() {
        super(0, null, null, null, null, null);
    }

    public Wine(int id, ItemName name, ItemType type, ItemSeller seller, ItemDetails details, BeverageDetails beverage) {
        super(id, name, type, seller, details, beverage);
    }

    public boolean isRed() {
        return type.isType(ItemTypes.RedWine);
    }

    public boolean isRose() {
        return type.isType(ItemTypes.RoseWine);
    }

    public boolean isWhite() {
        return type.isType(ItemTypes.WhiteWine);
    }

    public boolean isSparkling() {
        return type.isType(ItemTypes.SparklingWine);
    }
    
    public String getSearchQuery() {
        return getName() + " " + getCountry() + " " + getArea() + " " + getYear();
    }

    @Override
    public String toString() {
        return getName() + " " + getCountry() + " " + getArea() + " " + getType().text.replaceAll("[,&]", "" + " " + getYear()).toLowerCase();
    }
}
