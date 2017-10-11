package com.gitlab.uu.vinproffsen.ui.table;

import com.gitlab.uu.vinproffsen.Utility;
import com.gitlab.uu.vinproffsen.db.WineResult;
import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.ui.WineListPopup;
import com.gitlab.uu.vinproffsen.ui.presenters.TablePresenter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Wine table.
 *
 * Note: Pagination is not implemented yet.
 */
public class WineTable extends JPanel {
    private final JTable wineTable;
    private final WineTableModel listModel;
    private final TablePresenter presenter;

    private JLabel showingLabel;
    private JLabel pageLabel;

    private JButton first;
    private JButton previous;
    private JButton next;
    private JButton last;

    public WineTable(TablePresenter presenter) {
        this.presenter = presenter;

        setLayout(new BorderLayout());

        // Set alternating background color for table rows
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        defaults.putIfAbsent("Table.alternateRowColor", new Color(230, 230, 230));

        first = new JButton(new AbstractAction("<< Första") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPage(1);
            }
        });
        previous = new JButton(new AbstractAction("< Föregående") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPage(listModel.getCurrentPage() - 1);
            }
        });
        next = new JButton(new AbstractAction("Nästa >") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPage(listModel.getCurrentPage() + 1);
            }
        });
        last = new JButton(new AbstractAction("Sista >>") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPage(listModel.getTotalPages());
            }
        });

        listModel = new WineTableModel(presenter.getView());

        wineTable = new JTable();
        wineTable.setModel(listModel);
        wineTable.setShowGrid(false);
        wineTable.setRowHeight(25);

        wineTable.getTableHeader().setReorderingAllowed(false);
        wineTable.getTableHeader().setDefaultRenderer(new HeaderColumnCellRenderer(wineTable));
        wineTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                presenter.tableHeaderClicked(e);
                updateColumnWidths();
            }
        });

        wineTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                presenter.tableClicked(e);
            }
        });

        wineTable.setComponentPopupMenu(new WineListPopup(presenter.getView()));

        // set all cells enabled/disabled to the same as their table
        wineTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                setEnabled(table.isEnabled());

                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            }
        });

        wineTable.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(new JScrollPane(wineTable), BorderLayout.CENTER);
        add(createPaginationControls(), BorderLayout.SOUTH);

        updateColumnWidths();
    }

    public void setTableEnabled(boolean enabled) {
        wineTable.setEnabled(enabled);
    }

    private JPanel createPaginationControls() {
        JPanel panel = new JPanel();

        showingLabel = new JLabel();
        pageLabel = new JLabel();

        updatePagination();

        panel.add(showingLabel);
        panel.add(first);
        panel.add(previous);
        panel.add(pageLabel);
        panel.add(next);
        panel.add(last);

        return panel;
    }

    public void setPage(int page) {
        if (listModel.setPage(page)) {
            updatePagination();
            presenter.pageChanged(listModel.getCurrentPage());
        }
    }

    public void setSortedBy(WineTableColumn column) {
        listModel.setSortedBy(column);
    }

    public void setAscending(boolean ascending) {
        listModel.setAscending(ascending);
    }

    private void updatePagination() {
        showingLabel.setText("Visar " + Utility.pluralize(listModel.getRowCount(), "vin", "viner") + " av totalt " + listModel.getTotalCount() + ".");
        pageLabel.setText("Sida " + listModel.getCurrentPage() + " av " + listModel.getTotalPages());

        first.setEnabled(getCurrentPage() != 1);
        previous.setEnabled(first.isEnabled());

        next.setEnabled(getCurrentPage() != listModel.getTotalPages());
        last.setEnabled(next.isEnabled());
    }

    public int getWinesPerPage() {
        return listModel.getWinesPerPage();
    }

    public int getCurrentPage() {
        return listModel.getCurrentPage();
    }

    /**
     * Update wine list of table.
     * @param result result
     */
    public void setWines(WineResult result) {
        listModel.setWines(result);
        wineTable.getTableHeader().updateUI();

        updatePagination();
    }

    /**
     * Force column widths to be different based on column type.
     */
    private void updateColumnWidths() {
        TableColumnModel columnModel = wineTable.getColumnModel();

        for (int i = 0; i < WineTableModel.COLUMNS.length; i++) {
            switch (WineTableModel.COLUMNS[i]) {
                case FullName:
                    columnModel.getColumn(i).setPreferredWidth(300);
                    break;

                case Type:
                    columnModel.getColumn(i).setPreferredWidth(120);
                    break;

                case From:
                    columnModel.getColumn(i).setPreferredWidth(180);
                    break;

                case Volume:
                case Alcohol:
                case Ecological:
                case Kosher:
                case Price:
                case Year:
                    columnModel.getColumn(i).setPreferredWidth(10);
                    break;
            }
        }
    }

    public WineTableColumn getSortedBy() {
        return ((WineTableModel) wineTable.getModel()).getSortedBy();
    }

    public boolean isAscending() {
        return ((WineTableModel) wineTable.getModel()).isAscending();
    }

    public List<Wine> getSelectedWines() {
        List<Wine> selected = new ArrayList<>();

        WineTableModel dataModel = (WineTableModel) wineTable.getModel();

        for (int row : wineTable.getSelectedRows())
            if (dataModel.getWine(row) != null)
                selected.add(dataModel.getWine(row));

        return selected;
    }
}
