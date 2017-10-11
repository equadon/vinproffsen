package com.gitlab.uu.mvp;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Model base class.
 *
 * @author Niklas Persson
 * @version 2016-01-29
 */
public abstract class Model {
    private final Application application;

    /**
     * Constructor.
     * @param application application object
     */
    public Model(Application application) {
        this.application = application;
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
