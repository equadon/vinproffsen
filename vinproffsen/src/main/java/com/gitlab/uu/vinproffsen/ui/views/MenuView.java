package com.gitlab.uu.vinproffsen.ui.views;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.View;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.ui.presenters.MenuPresenter;

import javax.swing.*;

/**
 * View that renders the menu.
 *
 * @author Niklas Persson
 * @version 2016-03-12
 */
public class MenuView extends View<WineModel, MenuPresenter> {
    /**
     * Constructor and pass model to presenter.
     *
     * @param application application object.
     * @param model       model to pass to the presenter.
     */
    public MenuView(Application application, WineModel model) {
        super(application, model);
    }

    public JMenuBar render() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("Arkiv");

        JMenuItem newWine = new JMenuItem("Nytt vin");
        newWine.addActionListener((e) -> presenter.createWine());

        JMenuItem exit = new JMenuItem("Avsluta");
        exit.addActionListener((e) -> presenter.quit());

        file.add(newWine);
        file.addSeparator();
        file.add(exit);

        bar.add(file);

        return bar;
    }

    @Override
    protected MenuPresenter createPresenter(WineModel model) {
        return new MenuPresenter(application, this, model);
    }
}
