package com.gitlab.uu.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.LinkedHashMap;

/**
 * A chart representing a bar chart.
 *
 * @author Niklas Persson
 * @version 2016-02-06
 */
public class BarChart extends Chart {
    protected String categoriesLabel;
    protected String valuesLabel;

    /**
     * Bar chart constructor.
     * @param title chart title.
     * @param categoriesLabel categories label (x-axis).
     * @param valuesLabel values label (y-axis).
     * @param legend enable legend.
     * @param tooltips enable tooltips
     */
    public BarChart(String title, String categoriesLabel, String valuesLabel, boolean legend, boolean tooltips) {
        super(title, legend, tooltips);

        this.categoriesLabel = categoriesLabel;
        this.valuesLabel = valuesLabel;

        update();
    }

    /**
     * Getter for categories label.
     * @return categories label.
     */
    public String getCategoriesLabel() {
        return categoriesLabel;
    }

    /**
     * Set categories label.
     * @param categoriesLabel new categories label.
     */
    public void setCategoriesLabel(String categoriesLabel) {
        this.categoriesLabel = categoriesLabel;

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.getDomainAxis().setLabel(categoriesLabel);
    }

    /**
     * Values label getter.
     * @return values label.
     */
    public String getValuesLabel() {
        return valuesLabel;
    }

    /**
     * Set values label.
     * @param valuesLabel new values label.
     */
    public void setValuesLabel(String valuesLabel) {
        this.valuesLabel = valuesLabel;

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.getRangeAxis().setLabel(valuesLabel);
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

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        if (enabled) {
            renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        } else {
            renderer.setBaseToolTipGenerator(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinkedHashMap<String, Number> getData() {
        DefaultCategoryDataset dataset = (DefaultCategoryDataset) getDataset();

        String[] labels = (String[]) dataset.getColumnKeys().toArray(new String[dataset.getColumnCount()]);

        LinkedHashMap<String, Number> map = new LinkedHashMap<>(labels.length);
        for (int i = 0; i < labels.length; i++) {
            map.put(labels[i], dataset.getValue(0, i));
        }

        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addData(String label, double value) {
        addData(label, getTitle(), value);
    }

    public void addData(String label, String key, double value) {
        if (!label.equals("")) {
            DefaultCategoryDataset dataset = (DefaultCategoryDataset) getDataset();

            if (dataset != null) {
                dataset.setValue(value, key, label);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateData(String[] labels, String[] values) {
        DefaultCategoryDataset dataset = (DefaultCategoryDataset) getDataset();

        if (dataset != null) {
            dataset.clear();

            for (int i = 0; i < labels.length; i++) {
                if (!labels[i].equals("")) {
                    try {
                        dataset.setValue(Float.valueOf(values[i]), getTitle(), labels[i]);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        dataset.setValue(0, getTitle(), labels[i]);
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
        return ChartFactory.createBarChart(getTitle(), categoriesLabel, valuesLabel, new DefaultCategoryDataset(), PlotOrientation.VERTICAL,
                getLegend(), getTooltips(), false);
    }
}
