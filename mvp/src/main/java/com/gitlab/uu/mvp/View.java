package com.gitlab.uu.mvp;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Abstract base view class.
 *
 * @author Niklas Persson
 * @version 2016-02-07
 */
public abstract class View<M extends Model, P extends Presenter> {
    protected final Application application;
    protected final P presenter;

    /**
     * Constructor and pass model to presenter.
     * @param application application object.
     * @param model model to pass to the presenter.
     */
    public View(Application application, M model) {
        this.application = application;

        presenter = createPresenter(model);
    }

    /**
     * Get application.
     * @return Application
     */
    public Application getApplication() {
        return application;
    }

    /**
     * Getter for the presenter for this view.
     * @return presenter of type P.
     */
    public P getPresenter() {
        return presenter;
    }

    /**
     * Create the presenter object.
     * @return presenter object of type P.
     */
    protected abstract P createPresenter(M model);

    /**
     * Easy access for {@link EventAggregator#send(String)}.
     */
    public boolean send(String event) {
        return application.getEvents().send(event);
    }

    /**
     * Easy access for {@link EventAggregator#send(String, Object)}.
     */
    @SuppressWarnings("unchecked")
    public <T> boolean send(String event, T data) {
        return application.getEvents().send(event, data);
    }

    /**
     * Easy access for {@link EventAggregator#listen(String, Consumer)}.
     */
    @SuppressWarnings("unchecked")
    public <T> boolean listen(String event, Consumer<T> consumer) {
        return application.getEvents().listen(event, consumer);
    }

    /**
     * Easy access for {@link EventAggregator#listen(String, Consumer)}.
     */
    @SuppressWarnings("unchecked")
    public <T, U> boolean listen(String event, BiConsumer<T, U> consumer) {
        return application.getEvents().listen(event, consumer);
    }

    /**
     * Easy access for {@link EventAggregator#ignore(String, Consumer)}.
     */
    @SuppressWarnings("unchecked")
    public <T> boolean ignore(String event, Consumer<T> consumer) {
        return application.getEvents().ignore(event, consumer);
    }
}