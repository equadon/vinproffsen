package com.gitlab.uu.vinproffsen.ui;

/**
 * A default combobox item storing a key/value pair.
 */
public class ComboBoxItem {
    public final String id;
    public final String value;

    public ComboBoxItem(String id, String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
