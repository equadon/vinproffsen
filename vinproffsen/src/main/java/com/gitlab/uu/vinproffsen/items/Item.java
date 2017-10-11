package com.gitlab.uu.vinproffsen.items;

import com.gitlab.uu.vinproffsen.items.storage.ItemDetails;
import com.gitlab.uu.vinproffsen.items.storage.ItemName;
import com.gitlab.uu.vinproffsen.items.storage.ItemSeller;
import com.gitlab.uu.vinproffsen.items.storage.ItemType;

/**
 * Base class for all items. For now we are only interested in wines but can easily support other types later.
 *
 * Each item holds information in storage classes which allows us to create different items with less parameters.
 *
 * @author Niklas Persson
 * @version 2016-03-12
 */
public abstract class Item {
    protected int id;

    protected ItemName name;
    protected ItemType type;
    protected ItemSeller seller;
    protected ItemDetails details;

    public Item(int id, ItemName name, ItemType type, ItemSeller seller, ItemDetails details) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.seller = seller;
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public ItemName getFullName() {
        return name;
    }

    public String getName() {
        return name.name;
    }

    public String getAlternativeName() {
        return name.alternativeName;
    }

    public ItemType getType() {
        return type;
    }

    public int getPrice() {
        return seller.price;
    }

    public String getDescription() {
        return details.description;
    }

    public String getSellStart() {
        return seller.sellStart;
    }
}
