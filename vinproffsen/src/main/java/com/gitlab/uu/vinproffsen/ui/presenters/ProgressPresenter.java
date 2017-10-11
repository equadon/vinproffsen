package com.gitlab.uu.vinproffsen.ui.presenters;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.Presenter;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.ui.views.ProgressView;

/**
 * Presenter that takes care of the progress bar being show while loading the database.
 *
 * @author Niklas Persson
 * @version 2016-03-17
 */
public class ProgressPresenter extends Presenter<WineModel, ProgressView> {
    public ProgressPresenter(Application application, ProgressView view, WineModel model) {
        super(application, view, model);
    }
}
