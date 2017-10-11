package com.gitlab.uu.vinproffsen.ui.presenters;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.Presenter;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.ui.views.MenuView;
import com.gitlab.uu.vinproffsen.ui.views.ViewTabs;

/**
 * Presenter that takes care of the main menu.
 *
 * @author Niklas Persson
 * @version 2016-03-15
 */
public class MenuPresenter extends Presenter<WineModel, MenuView> {
    public MenuPresenter(Application application, MenuView view, WineModel model) {
        super(application, view, model);
    }

    public void createWine() {
        send("wine:tab", ViewTabs.AddWine);
    }

    public void quit() {
        System.exit(0);
    }
}
