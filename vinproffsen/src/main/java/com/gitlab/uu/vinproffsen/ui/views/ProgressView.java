package com.gitlab.uu.vinproffsen.ui.views;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.View;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.ui.presenters.ProgressPresenter;

import javax.swing.*;
import java.awt.*;

/**
 * View that renders the progress bar dialog.
 *
 * @author Niklas Persson
 * @version 2016-03-12
 */
public class ProgressView extends View<WineModel, ProgressPresenter> {
    private JFrame parent;
    private JDialog dialog;
    private JProgressBar progress;
    private JLabel statusLabel;

    public ProgressView(Application application, WineModel model, JFrame parent) {
        super(application, model);

        this.parent = parent;
    }

    public JDialog show() {
        dialog = new JDialog(parent, "Läser in viner...", true);
        progress = new JProgressBar();

        listen("wine:progress", (n, total) -> {
            if (progress.isIndeterminate()) {
                progress.setIndeterminate(false);
                statusLabel.setText("Läser in viner...");
            }

            progress.setMaximum((int) total);
            progress.setValue((int) n);
        });

        progress.setIndeterminate(true);
        progress.addChangeListener(e -> {
            if (progress.getValue() == progress.getMaximum()) {
                dialog.setVisible(false);
            }
        });
        progress.setStringPainted(true);

        statusLabel = new JLabel("Initierar...");

        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(400, 80);

        dialog.add(statusLabel);
        dialog.add(progress);

        dialog.setVisible(true);

        return dialog;
    }

    @Override
    protected ProgressPresenter createPresenter(WineModel model) {
        return null;
    }
}
