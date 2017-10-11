package com.gitlab.uu.vinproffsen.ui.presenters;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.Presenter;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.ui.views.MainView;

/**
 * Presenter that takes care of the main window.
 *
 * @author Niklas Persson
 * @version 2016-03-14
 */
public class MainPresenter extends Presenter<WineModel, MainView> {
    public MainPresenter(Application application, MainView view, WineModel model) {
        super(application, view, model);

        listen("wine:status:left", (message) -> view.setLeftStatus((String) message));
        listen("wine:status:right", (message) -> view.setRightStatus((String) message));
    }
}
