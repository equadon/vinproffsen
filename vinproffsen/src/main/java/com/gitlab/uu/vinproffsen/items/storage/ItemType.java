package com.gitlab.uu.vinproffsen.items.storage;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ItemType {
    public final String text;

    private final EnumSet<ItemTypes> types;

    public ItemType(String text) {
        this.text = text;

        Set<ItemTypes> itemSet = new HashSet<>();
        String lower = text.toLowerCase().trim();

        if (lower.contains("mousserande"))
            itemSet.add(ItemTypes.SparklingWine);
        if (lower.contains("rött"))
            itemSet.add(ItemTypes.RedWine);
        if (lower.contains("vitt"))
            itemSet.add(ItemTypes.WhiteWine);
        if (lower.contains("rosé"))
            itemSet.add(ItemTypes.RoseWine);

        if (itemSet.isEmpty())
            itemSet.add(ItemTypes.Unknown);

        types = EnumSet.copyOf(itemSet);
    }

    public boolean isType(ItemTypes type) {
        return types.contains(type);
    }

    public boolean isWine() {
        return isType(ItemTypes.RedWine) || isType(ItemTypes.WhiteWine) || isType(ItemTypes.SparklingWine) || isType(ItemTypes.RoseWine);
    }

    @Override
    public String toString() {
        return text;
    }
}
