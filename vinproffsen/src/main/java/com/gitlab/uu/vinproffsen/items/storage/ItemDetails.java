package com.gitlab.uu.vinproffsen.items.storage;

/**
 * Storage class. Holds item details.
 *
 * @author Niklas Persson
 * @version 2016-03-13
 */
public class ItemDetails {
    public final Boolean ecological;
    public final Boolean kosher;
    public final String assortment;
    public final String description;

    public ItemDetails(Boolean ecological, Boolean kosher, String assortment, String description) {
        this.ecological = ecological;
        this.kosher = kosher;
        this.assortment = assortment;
        this.description = description;
    }
}
