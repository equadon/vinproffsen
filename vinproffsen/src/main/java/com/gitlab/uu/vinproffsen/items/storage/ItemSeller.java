package com.gitlab.uu.vinproffsen.items.storage;

/**
 * Storage class. Holds seller information.
 *
 * @author Niklas Persson
 * @version 2016-03-13
 */
public class ItemSeller {
    public final String sellStart;

    public final String area;
    public final String country;
    public final String producer;
    public final String supplier;
    public final Integer year;

    public final Integer price; // incl. VAT in öre

    public ItemSeller(String sellStart, String area, String country, String producer, String supplier, Integer year, Integer price) {
        this.sellStart = sellStart;
        this.area = area;
        this.country = country;
        this.producer = producer;
        this.supplier = supplier;
        this.year = year;
        this.price = price;
    }
}
