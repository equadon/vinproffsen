package com.gitlab.uu.vinproffsen.ui.views;

import com.gitlab.uu.charts.PieChart;
import com.gitlab.uu.charts.StackedBarChart;
import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.View;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.db.WineResult;
import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.ui.presenters.StartScreenPresenter;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * View that renders the welcome screen.
 *
 * @author Niklas Persson
 * @version 2016-03-12
 */
public class StartScreenView extends View<WineModel, StartScreenPresenter> {
    private JPanel chartsPanel;

    private JTextField startSearchText;

    private Wine randomWine;
    private final JLabel randomWineLabel;

    public StartScreenView(Application application, WineModel model) {
        super(application, model);

        chartsPanel = new JPanel();
        chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.X_AXIS));

        randomWineLabel = new JLabel();
    }

    public JPanel getChartsPanel() {
        return chartsPanel;
    }

    public JPanel render(WineTableView tableView) {
        startSearchText = tableView.createSearchField("Sök vin...");
        startSearchText.addActionListener((e) -> send("wine:search", startSearchText.getText()));
        startSearchText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                startSearchText.setText("");
            }
        });
        
        for (KeyListener listener : startSearchText.getKeyListeners())
            startSearchText.removeKeyListener(listener);

        JLabel title = new JLabel("Välkommen till VinProffsen!");
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 34));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(title, BorderLayout.NORTH);

        JLabel sponsorTitle = new JLabel("Ett vin från vårt sortiment:");
        sponsorTitle.setFont(new Font(sponsorTitle.getFont().getFontName(), Font.BOLD, 24));

        JButton showWineButton = new JButton(new AbstractAction("Visa") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                send("wine:search", randomWine.getSearchQuery());
            }
        });

        JPanel sponsored = new JPanel();
        sponsored.add(sponsorTitle);
        sponsored.add(randomWineLabel);
        sponsored.add(showWineButton);

        JPanel searchPanel = new JPanel();
        searchPanel.add(startSearchText);

        JPanel sponsorPanel = new JPanel(new BorderLayout());
        sponsorPanel.add(searchPanel, BorderLayout.NORTH);
        sponsorPanel.add(sponsored, BorderLayout.CENTER);

        panel.add(sponsorPanel, BorderLayout.CENTER);
        panel.add(chartsPanel, BorderLayout.SOUTH);

        send("wine:update:charts");
        send("wine:update:random");

        return panel;
    }

    public void updateEcoWineChart(int redCount, int whiteCount, int sparklingCount, int roseCount, int unknownCount) {
        int totalCount = redCount + whiteCount + sparklingCount + roseCount;

        PieChart pieChart = new PieChart("Ekologiska Viner (" + totalCount + " st)", true, true);
        pieChart.addData("Rött vin (" + redCount + " st)", redCount);
        pieChart.addData("Vitt vin (" + whiteCount + " st)", whiteCount);
        pieChart.addData("Mousserande vin (" + sparklingCount + " st)", sparklingCount);
        pieChart.addData("Rosévin (" + roseCount + " st)", roseCount);

        if (unknownCount > 0)
            pieChart.addData("Övriga (" + unknownCount + " st)", unknownCount);

        // Disable right click menu
        ChartPanel chartPanel = pieChart.getPanel();
        chartPanel.setPopupMenu(null);

        chartsPanel.add(chartPanel);
    }

    public void updateWineCountChart(int totalCount, Map<String, Map<String, Integer>> types) {
        StackedBarChart barChart = new StackedBarChart("Vinsortiment (" + totalCount + " st)", "Vintyp", "Antal viner", true, true);

        for (Map.Entry<String, Map<String, Integer>> entry : types.entrySet()) {
            barChart.addData(entry.getKey(), "Rött vin", entry.getValue().get("Rött"));
            barChart.addData(entry.getKey(), "Vitt vin", entry.getValue().get("Vitt"));
            barChart.addData(entry.getKey(), "Mousserande vin", entry.getValue().get("Mousserande"));
            barChart.addData(entry.getKey(), "Rosévin", entry.getValue().get("Rosé"));
        }

        ChartPanel chartPanel = barChart.getPanel();
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setPopupMenu(null);

        chartsPanel.add(chartPanel);
    }

    @Override
    protected StartScreenPresenter createPresenter(WineModel model) {
        return new StartScreenPresenter(application, this, model);
    }

    public void updateRandomWine(WineResult result) {
        try {
            randomWine = result.wines.get(0);
            randomWineLabel.setText(randomWine.toString());
        } catch (NullPointerException ignored) {}
    }
}
