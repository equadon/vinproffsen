package com.gitlab.uu.vinproffsen.items.storage;

/**
 * Storage class. Holds item names.
 *
 * @author Niklas Persson
 * @version 2016-03-13
 */
public class ItemName {
    public final String name;
    public final String alternativeName;

    public ItemName(String name, String alternativeName) {
        this.name = name;
        this.alternativeName = alternativeName;
    }

    @Override
    public String toString() {
        String text = name;
        if (alternativeName != null && !alternativeName.equals(""))
            text = text + " (" + alternativeName + ")";
        return text;
    }
}
