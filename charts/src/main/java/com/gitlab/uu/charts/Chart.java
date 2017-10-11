package com.gitlab.uu.charts;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.*;
import org.jfree.data.general.Dataset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

/**
 * Base class for all charts.
 *
 * @author Niklas Persson
 * @version 2016-02-06
 */
public abstract class Chart {
    private static final Logger LOG = Logger.getLogger(Chart.class.getName());

    protected JFreeChart chart;

    private String title;

    protected boolean legend;
    protected boolean tooltips;

    /**
     * Chart constructor.
     * @param title chart title.
     * @param legend enable legend.
     * @param tooltips enable tooltips
     */
    public Chart(String title, boolean legend, boolean tooltips) {
        this.title = title;
        this.legend = legend;
        this.tooltips = tooltips;

        update();
    }

    /**
     * Getter for chart title.
     * @return chart title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set chart title.
     * @param title new chart title.
     */
    public void setTitle(String title) {
        this.title = title;
        chart.setTitle(title);
    }

    /**
     * Getter for chart legend.
     * @return true if legend is enabled.
     */
    public boolean getLegend() {
        return legend;
    }

    /**
     * Enable chart legend.
     * @param enabled true to enable chart legend.
     */
    public abstract void enableLegend(boolean enabled);

    /**
     * Getter for chart tooltips.
     * @return true if tooltips is enabled.
     */
    public boolean getTooltips() {
        return tooltips;
    }

    /**
     * Enable chart tooltips.
     * @param enabled true to enable chart tooltips.
     */
    public abstract void enableTooltips(boolean enabled);

    public abstract LinkedHashMap<String, Number> getData();

    public abstract void addData(String label, double value);

    /**
     * Update data with the new labels and values.
     * @param labels array of data labels.
     * @param values array of data values.
     */
    public abstract void updateData(String[] labels, String[] values);

    /**
     * Update chart.
     */
    protected void update() {
        chart = generateChart();

        if (chart != null)
            chart.setBackgroundPaint(null); // transparent background
    }

    /**
     * Return a ChartPanel of the chart.
     * @return ChartPanel.
     */
    public ChartPanel getPanel() {
        return new ChartPanel(chart, true);
    }

    /**
     * Return image of chart with default width and height.
     * @return chart image as a buffered image.
     */
    public BufferedImage getImage() {
        return getImage(ChartPanel.DEFAULT_WIDTH, ChartPanel.DEFAULT_HEIGHT);
    }

    /**
     * Return image of chart.
     * @param width image width.
     * @param height image height.
     * @return chart image as a buffered image.
     */
    public BufferedImage getImage(int width, int height) {
        return chart.createBufferedImage(width, height);
    }

    /**
     * Save chart as an image to disk using default width and height.
     *
     * The filetype will be determined by the filename. Supported types: PNG and JPEG.
     * @param filename image filename.
     * @throws IOException if filename has an unsupported filename.
     */
    public void saveAsImage(String filename) throws IOException {
        saveAsImage(filename, ChartPanel.DEFAULT_WIDTH, ChartPanel.DEFAULT_HEIGHT);
    }

    /**
     * Save chart as an image to disk.
     *
     * The filetype will be determined by the filename. Supported types: PNG and JPEG.
     * @param filename image filename.
     * @param width image width.
     * @param height image height.
     * @throws IOException if filename has an unsupported filename.
     */
    public void saveAsImage(String filename, int width, int height) throws IOException {
        File file = new File(filename);

        if (filename.endsWith(".png")) {
            ChartUtilities.saveChartAsPNG(file, chart, width, height);

            LOG.fine("Successfully saved PNG image to: " + filename);
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            ChartUtilities.saveChartAsJPEG(file, chart, width, height);

            LOG.fine("Successfully saved JPG image to: " + filename);
        } else {
            throw new IOException("Unknown image type for file: " + filename);
        }
    }

    /**
     * Get dataset of chart.
     *
     * Source: http://www.jfree.org/forum/viewtopic.php?f=3&t=13323
     *
     * @return Dataset of chart or null.
     */
    protected Dataset getDataset() {
        Dataset result = null;

        if (chart != null) {
            Plot plot = chart.getPlot();
            if (plot instanceof CategoryPlot) {
                result = ((CategoryPlot) plot).getDataset();
            } else if (plot instanceof MeterPlot) {
                result = ((MeterPlot) plot).getDataset();
            } else if (plot instanceof PiePlot) {
                result = ((PiePlot) plot).getDataset();
            } else if (plot instanceof ThermometerPlot) {
                result = ((ThermometerPlot) plot).getDataset();
            } else if (plot instanceof XYPlot) {
                result = ((XYPlot) plot).getDataset();
            }
        }

        return result;
    }

    /**
     * Generate a new chart.
     * @return JFreeChart.
     */
    protected abstract JFreeChart generateChart();
}
