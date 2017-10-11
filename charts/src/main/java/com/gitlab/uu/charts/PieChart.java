package com.gitlab.uu.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import java.util.LinkedHashMap;

/**
 * A chart representing a pie chart.
 *
 * @author Niklas Persson
 * @version 2016-02-07
 */
public class PieChart extends Chart {
    /**
     * Pie chart constructor.
     * @param title chart title.
     * @param legend enable legend.
     * @param tooltips enable tooltips
     */
    public PieChart(String title, boolean legend, boolean tooltips) {
        super(title, legend, tooltips);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableLegend(boolean enabled) {
        this.legend = enabled;

        update();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableTooltips(boolean enabled) {
        this.tooltips = enabled;

        PiePlot plot = (PiePlot) chart.getPlot();

        if (enabled) {
            plot.setToolTipGenerator(new StandardPieToolTipGenerator());
        } else {
            plot.setToolTipGenerator(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinkedHashMap<String, Number> getData() {
        DefaultPieDataset dataset = (DefaultPieDataset) getDataset();

        String[] labels = (String[]) dataset.getKeys().toArray(new String[dataset.getItemCount()]);

        LinkedHashMap<String, Number> map = new LinkedHashMap<>(labels.length);
        for (int i = 0; i < labels.length; i++) {
            map.put(labels[i], dataset.getValue(i));
        }

        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addData(String label, double value) {
        if (!label.equals("")) {
            DefaultPieDataset dataset = (DefaultPieDataset) getDataset();

            if (dataset != null) {
                dataset.setValue(label, value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateData(String[] labels, String[] values) {
        DefaultPieDataset dataset = (DefaultPieDataset) getDataset();

        if (dataset != null) {
            dataset.clear();

            for (int i = 0; i < labels.length; i++) {
                if (!labels[i].equals("")) {
                    try {
                        dataset.setValue(labels[i], Float.valueOf(values[i]));
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        dataset.setValue(labels[i], 0);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JFreeChart generateChart() {
        return ChartFactory.createPieChart(getTitle(), new DefaultPieDataset(), getLegend(), getTooltips(), false);
    }
}
