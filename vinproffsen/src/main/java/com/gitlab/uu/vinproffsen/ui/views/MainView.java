package com.gitlab.uu.vinproffsen.ui.views;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.View;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.WineSettings;
import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.ui.MenuButton;
import com.gitlab.uu.vinproffsen.ui.presenters.MainPresenter;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.util.logging.Logger;

/**
 * View that renders the main window and creates the different tabs.
 *
 * @author Niklas Persson
 * @version 2016-03-12
 */
public class MainView extends View<WineModel, MainPresenter> {
    private final static Logger LOG = Logger.getLogger(MainView.class.getName());

    private final ImageIcon startIcon;
    private final ImageIcon searchIcon;
    private final ImageIcon addIcon;
    private final ImageIcon emailIcon;

    private final JPanel mainPanel;
    private JPanel centerPanel;

    private final JLabel statusLeft;
    private final JLabel statusRight;

    private JToggleButton welcome;
    private JToggleButton table;
    private JToggleButton addWine;
    private JToggleButton email;

    public MainView(Application application, WineModel model) {
        super(application, model);

        listen("wine:search", (query) -> showTab(ViewTabs.WineTable));
        listen("wine:tab", tab -> showTab((ViewTabs) tab));

        // Load icons
        startIcon = loadIcon("icons/48x48/House.png");
        searchIcon = loadIcon("icons/48x48/Search.png");
        addIcon = loadIcon("icons/48x48/Edit.png");
        emailIcon = loadIcon("icons/48x48/Save.png");

        mainPanel = new JPanel(new BorderLayout());

        statusLeft = new JLabel("");
        statusRight = new JLabel("");
    }

    private void showTab(ViewTabs tab) {
        CardLayout layout = (CardLayout) centerPanel.getLayout();

        layout.show(centerPanel, tab.toString());

        if (tab == ViewTabs.StartScreen)
            welcome.doClick();
        else if (tab == ViewTabs.AddWine)
            addWine.doClick();
        else if (tab == ViewTabs.WineTable)
            table.doClick();
        else if (tab == ViewTabs.SavedWineList)
            email.doClick();
    }

    private ImageIcon loadIcon(String resourceName) {
        ImageIcon icon = null;

        URL house = MainView.class.getClassLoader().getResource(resourceName);
        if (house == null) {
            LOG.warning("Warning: Unable to load icon: " + resourceName);
        } else {
            icon = new ImageIcon(house);
        }

        return icon;
    }

    /**
     * Set left status bar message.
     * @param message message
     */
    public void setLeftStatus(String message) {
        statusLeft.setText(message);
    }

    /**
     * Set right status bar message.
     * @param message message
     */
    public void setRightStatus(String message) {
        statusRight.setText(message);
    }

    /**
     * Create bottom status bar.
     */
    private JPanel createStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBorder(new BevelBorder(BevelBorder.LOWERED));
        status.setPreferredSize(new Dimension(mainPanel.getWidth(), 20));

        Font statusFont = statusLeft.getFont();

        statusLeft.setHorizontalAlignment(SwingConstants.LEFT);
        statusLeft.setFont(new Font(statusFont.getName(), Font.PLAIN, statusFont.getSize()));

        statusRight.setHorizontalAlignment(SwingConstants.RIGHT);
        statusRight.setFont(new Font(statusFont.getName(), Font.ITALIC, statusFont.getSize()));

        status.add(statusLeft, BorderLayout.WEST);
        status.add(statusRight, BorderLayout.EAST);

        return status;
    }

    /**
     * Create menu toolbar.
     */
    private JToolBar createToolBar() {
        WineSettings settings = WineSettings.getInstance();

        ButtonGroup group = new ButtonGroup();

        welcome = createToolBarButton(ViewTabs.StartScreen, startIcon, "Start", "Startskärmen");
        table = createToolBarButton(ViewTabs.WineTable, searchIcon, "Sök", "Sök vin");
        addWine = createToolBarButton(ViewTabs.AddWine, addIcon, "Lägg till", "Lägg till vin");
        email = createToolBarButton(ViewTabs.SavedWineList, emailIcon, "Email", "Maila vinlistan");

        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.setRollover(true);

        bar.add(welcome);
        group.add(welcome);

        bar.add(table);
        group.add(table);

        if (settings.isCustomer()) {
            bar.add(email);
            group.add(email);
        } else {
            bar.add(addWine);
            group.add(addWine);
        }

        return bar;
    }

    private JToggleButton createToolBarButton(ViewTabs tab, ImageIcon icon, String title, String tooltip) {
        CardLayout cards = (CardLayout) centerPanel.getLayout();

        JToggleButton button = new MenuButton(icon, title, tooltip);
        button.addActionListener((e) -> cards.show(centerPanel, tab.toString()));

        return button;
    }

    /**
     * Render the main window and its tabs.
     * @param wineView wine table view
     * @param startScreenView start screen view
     * @param savedListView user saved list view
     * @param addWineView add wine view
     * @return panel with everything combined
     */
    public JPanel render(WineTableView wineView, StartScreenView startScreenView, SavedListView savedListView, AddWineView addWineView) {
        WineSettings settings = WineSettings.getInstance();

        int width = settings.getInteger("view.main.width");
        int height = settings.getInteger("view.main.height");

        mainPanel.setPreferredSize(new Dimension(width, height));

        CardLayout layout = new CardLayout();

        centerPanel = new JPanel(layout);
        centerPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        centerPanel.add(ViewTabs.StartScreen.toString(), startScreenView.render(wineView));
        centerPanel.add(ViewTabs.WineTable.toString(), wineView.render());

        if (settings.isStaff()) {
            centerPanel.add(ViewTabs.AddWine.toString(), addWineView.render());
        } else {
            centerPanel.add(ViewTabs.SavedWineList.toString(), savedListView.render());
        }

        mainPanel.add(createToolBar(), BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(createStatusBar(), BorderLayout.SOUTH);

        showTab(ViewTabs.StartScreen);

        return mainPanel;
    }

    @Override
    protected MainPresenter createPresenter(WineModel model) {
        return new MainPresenter(application, this, model);
    }
}
