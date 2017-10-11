package com.gitlab.uu.mvp;

import java.util.function.Consumer;

/**
 * Presenter base class.
 * @param <V> view type used by this presenter
 */
public abstract class Presenter<M extends Model, V extends View> {
    private final Application application;
    protected final V view;
    protected final M model;

    /**
     * Construct a new presenter.
     * @param view view for this presenter
     */
    public Presenter(Application application, V view, M model) {
        this.application = application;
        this.view = view;
        this.model = model;
    }

    /**
     * Getter for the presenter view.
     * @return view of type V
     */
    public V getView() {
        return view;
    }

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
     * Easy access for {@link EventAggregator#send(String, Object, Object)}.
     */
    @SuppressWarnings("unchecked")
    public <T, U> boolean send(String event, T data1, U data2) {
        return application.getEvents().send(event, data1, data2);
    }

    /**
     * Easy access for {@link EventAggregator#listen(String, Consumer)}.
     */
    @SuppressWarnings("unchecked")
    public <T> boolean listen(String event, Consumer<T> consumer) {
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
