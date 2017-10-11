package com.gitlab.uu.vinproffsen.ui.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Custom table header renderer that shows sorting icons, both ascending and descending.
 *
 * @author Niklas Persson
 * @version 2016-03-22
 */
public class HeaderColumnCellRenderer implements TableCellRenderer {
    private final JTable table;

    private final Icon ascendingIcon;
    private final Icon descendingIcon;

    private final TableCellRenderer defaultRenderer;

    public HeaderColumnCellRenderer(JTable table) {
        this.table = table;
        defaultRenderer = table.getTableHeader().getDefaultRenderer();

        ascendingIcon = UIManager.getIcon("Table.ascendingSortIcon");
        descendingIcon = UIManager.getIcon("Table.descendingSortIcon");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component c = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        WineTableModel model = (WineTableModel) table.getModel();

        WineTableColumn sortedBy = model.getSortedBy();

        if (WineTableModel.COLUMNS[col] == sortedBy) {
            ((JLabel) c).setIcon(model.isAscending() ? ascendingIcon : descendingIcon);
        }

        return c;
    }
}
