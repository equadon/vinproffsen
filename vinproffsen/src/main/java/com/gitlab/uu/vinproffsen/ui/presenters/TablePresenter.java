package com.gitlab.uu.vinproffsen.ui.presenters;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.Model;
import com.gitlab.uu.mvp.Presenter;
import com.gitlab.uu.mvp.View;

import java.awt.event.MouseEvent;

public abstract class TablePresenter<M extends Model, V extends View> extends Presenter<M, V> {
    public TablePresenter(Application application, V view, M model) {
        super(application, view, model);
    }

    public abstract void tableHeaderClicked(MouseEvent e);
    public abstract void tableClicked(MouseEvent e);

    public abstract void pageChanged(int page);
}
