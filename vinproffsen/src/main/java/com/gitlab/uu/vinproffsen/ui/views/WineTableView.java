package com.gitlab.uu.vinproffsen.ui.views;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.View;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.db.WineResult;
import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.ui.ComboBoxItem;
import com.gitlab.uu.vinproffsen.ui.presenters.WineTablePresenter;
import com.gitlab.uu.vinproffsen.ui.table.WineTable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View that renders the wine table.
 *
 * @author Niklas Persson
 * @version 2016-03-14
 */
public class WineTableView extends View<WineModel, WineTablePresenter> {
    private static final Logger LOG = Logger.getLogger(WineTableView.class.getName());

    private static final int FILTER_OPEN_HEIGHT = 90;
    private static final int FILTER_CLOSED_HEIGHT = 50;

    private final WineTable wineTable;

    private final JTextField searchText;

    private final JCheckBox checkEco;
    private final JCheckBox checkKosher;

    private final JComboBox<ComboBoxItem> listCountries;
    private final JComboBox<ComboBoxItem> listAreas;
    private final JComboBox<ComboBoxItem> listTypes;

    private final KeyAdapter textKeyAdapter;

    private JSplitPane splitPane;

    private JTextField minPrice;
    private JTextField maxPrice;
    private JTextField minYear;
    private JTextField maxYear;

    private JLabel loadingLabel;
    private ImageIcon loadingIcon;
    private ImageIcon blankIcon;

    public WineTableView(Application application, WineModel model) {
        super(application, model);

        textKeyAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                presenter.searchWines(true);
            }
        };

        wineTable = new WineTable(presenter);
        searchText = createSearchField("");

        // checkboxes
        checkEco = new JCheckBox(new AbstractAction("Ekologiska") {
            @Override
            public void actionPerformed(ActionEvent e) {
                presenter.searchWines(true);
            }
        });
        checkKosher = new JCheckBox(new AbstractAction("Kosher") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.searchWines(true);
            }
        });

        // combo boxes
        listCountries = createCountriesList();
        listAreas = createAreasList();
        listTypes = createWineTypeList();

        listen("wine:search", query -> {
            searchText.setText((String) query);
            presenter.searchWines(true);
        });

        minPrice = new JTextField();
        maxPrice = new JTextField();
        minYear = new JTextField();
        maxYear = new JTextField();

        blankIcon = new ImageIcon(new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB));

        String loadingIconFilename = "icons/ajax-loader.gif";
        URL iconURL = getClass().getClassLoader().getResource(loadingIconFilename);
        if (iconURL == null) {
            LOG.log(Level.WARNING, "Unable to load loading icon: " + loadingIconFilename);

            loadingIcon = blankIcon;
        } else {
            loadingIcon = new ImageIcon(iconURL);
        }

        listen("wine:db:result", result -> {
            wineTable.setWines((WineResult) result);
            setTableLoading(false);
        });
    }

    /**
     * Getters for various components the presenter needs when searching.
     */
    public WineTable getTable() {
        return wineTable;
    }

    public String getSearchText() {
        return searchText.getText();
    }

    public Integer getMinYear() {
        try {
            Integer year = Integer.parseInt(minYear.getText());

            return year;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public Integer getMaxYear() {
        try {
            Integer year = Integer.parseInt(maxYear.getText());

            return year;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public Double getMinPrice() {
        try {
            Double price = Double.parseDouble(minPrice.getText());

            return price;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public Double getMaxPrice() {
        try {
            Double price = Double.parseDouble(maxPrice.getText());

            return price;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public boolean getEcological() {
        return checkEco.isSelected();
    }

    public boolean getKosher() {
        return checkKosher.isSelected();
    }

    public ComboBoxItem getSelectedCountry() {
        return (ComboBoxItem) listCountries.getSelectedItem();
    }

    public ComboBoxItem getSelectedArea() {
        return (ComboBoxItem) listAreas.getSelectedItem();
    }

    public ComboBoxItem getSelectedType() {
        return (ComboBoxItem) listTypes.getSelectedItem();
    }

    public List<Wine> getSelectedWines() {
        return wineTable.getSelectedWines();
    }

    public void setTableLoading(boolean loading) {
        loadingLabel.setIcon(loading ? loadingIcon : blankIcon);
    }

    /**
     * Create the filter panel with search options.
     */
    private JPanel createFilterPanel() {
        JPanel filter = new JPanel();

        GroupLayout layout = new GroupLayout(filter);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        filter.setLayout(layout);

        JToggleButton moreOptions = new JToggleButton("Avancerad sök");
        moreOptions.addActionListener(e -> {
            splitPane.setDividerLocation(moreOptions.isSelected() ? FILTER_OPEN_HEIGHT : FILTER_CLOSED_HEIGHT);
        });

        JLabel checkLabel = new JLabel("Visa endast: ");
        checkLabel.setFont(checkLabel.getFont().deriveFont(Font.PLAIN));

        JLabel searchLabel = new JLabel("Sök:");
        searchLabel.setFont(searchLabel.getFont().deriveFont(Font.PLAIN));

        JPanel yearPanel = createRangeFilter("Årgång:", minYear, maxYear);
        JPanel pricePanel = createRangeFilter("Pris:", minPrice, maxPrice);

        loadingLabel = new JLabel(blankIcon, JLabel.CENTER);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                      .addComponent(moreOptions)
                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                      .addComponent(listCountries)
                                      .addComponent(listAreas)
                               )
                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                      .addComponent(listTypes)
                                      .addComponent(yearPanel)
                               )
                      .addComponent(searchLabel)
                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                      .addComponent(searchText)
                                      .addComponent(pricePanel)
                               )
                      .addComponent(loadingLabel)
                      .addComponent(checkLabel)
                      .addComponent(checkEco)
                      .addComponent(checkKosher)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                      .addComponent(moreOptions)
                                      .addComponent(listTypes)
                                      .addComponent(listCountries)
                                      .addComponent(searchLabel)
                                      .addComponent(searchText)
                                      .addComponent(checkLabel)
                                      .addComponent(loadingLabel)
                                      .addComponent(checkEco)
                                      .addComponent(checkKosher)
                               )
                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                      .addComponent(listAreas)
                                      .addComponent(yearPanel)
                                      .addComponent(pricePanel)
                               )
                               );

        // Perform initial search so the table isn't empty
        presenter.searchWines(true);

        return filter;
    }

    private JPanel createRangeFilter(String labelText, JTextField minField, JTextField maxField) {
        JLabel priceLabel = new JLabel(labelText);

        minField.setColumns(5);
        minField.addKeyListener(textKeyAdapter);

        JLabel toLabel = new JLabel("till");
        toLabel.setFont(toLabel.getFont().deriveFont(Font.PLAIN));

        maxField.setColumns(5);
        maxField.addKeyListener(textKeyAdapter);

        JPanel panel = new JPanel();
        panel.add(priceLabel);
        panel.add(minField);
        panel.add(toLabel);
        panel.add(maxField);

        return panel;
    }

    public JTextField createSearchField(String text) {
        JTextField search = new JTextField();
        search.setToolTipText("Sök viner efter namn, vintyp och land.");
        search.setText(text);
        search.setColumns(20);
        search.setBorder(BorderFactory.createCompoundBorder(
                search.getBorder(),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)
        ));

        Font font = search.getFont();
        search.setFont(new Font(font.getFontName(), Font.PLAIN, 18));
        search.setForeground(new Color(100, 100, 100));

        search.addKeyListener(textKeyAdapter);

        return search;
    }

    /**
     * Render the table view.
     */
    public JComponent render() {
        JPanel filterPanel = createFilterPanel();

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(wineTable, BorderLayout.CENTER);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, filterPanel, tablePanel);
        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerLocation(FILTER_CLOSED_HEIGHT);

        searchText.grabFocus();

        return splitPane;
    }

    private JComboBox<ComboBoxItem> createCountriesList() {
        return createComboBox(presenter.getCountries());
    }

    private JComboBox<ComboBoxItem> createAreasList() {
        return createComboBox(presenter.getAreas());
    }

    private JComboBox<ComboBoxItem> createWineTypeList() {
        return createComboBox(presenter.getWineTypes());
    }

    private JComboBox<ComboBoxItem> createComboBox(DefaultComboBoxModel<ComboBoxItem> model) {
        JComboBox<ComboBoxItem> box = new JComboBox<>(model);
        box.addActionListener(e -> presenter.searchWines(true));

        return box;
    }

    @Override
    protected WineTablePresenter createPresenter(WineModel model) {
        return new WineTablePresenter(application, this, model);
    }
}
