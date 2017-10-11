package com.gitlab.uu.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * A chart representing a stacked bar chart.
 *
 * @author Niklas Persson
 * @version 2016-03-25
 */
public class StackedBarChart extends BarChart {
    /**
     * Bar chart constructor.
     *
     * @param title           chart title.
     * @param categoriesLabel categories label (x-axis).
     * @param valuesLabel     values label (y-axis).
     * @param legend          enable legend.
     * @param tooltips        enable tooltips
     */
    public StackedBarChart(String title, String categoriesLabel, String valuesLabel, boolean legend, boolean tooltips) {
        super(title, categoriesLabel, valuesLabel, legend, tooltips);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JFreeChart generateChart() {
        return ChartFactory.createStackedBarChart(getTitle(), categoriesLabel, valuesLabel, new DefaultCategoryDataset(), PlotOrientation.VERTICAL,
                                           getLegend(), getTooltips(), false);
    }
}
