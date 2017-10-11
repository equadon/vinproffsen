package com.gitlab.uu.mvp;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Event aggregator that handles processing events.
 * @param <T> data type.
 * @author Niklas Persson
 * @version 2016-02-07
 */
public class EventAggregator<T, U> {
    private static final Logger LOG = Logger.getLogger(EventAggregator.class.getName());

    private final Map<String, Set<Consumer<T>>> consumers = new HashMap<>();
    private final Map<String, Set<BiConsumer<T, U>>> biConsumers = new HashMap<>();

    /**
     * Send an event without any data.
     * @param event event name.
     * @return true if a consumer received the event.
     */
    public boolean send(String event) {
        return send(event, null);
    }

    /**
     * Send an event with data.
     * @param event event name.
     * @param data event data.
     * @return true if a consumer received the event.
     */
    public boolean send(String event, T data) {
        Set<Consumer<T>> consumers = this.consumers.get(event);

        if (consumers == null || consumers.isEmpty()) {
            return false;
        }

        for (Consumer<T> consumer : consumers) {
            consumer.accept(data);
        }

        if (data == null) {
            LOG.log(Level.FINE, "{0} sent to {1} {2}.", new Object[] {event, consumers.size(), consumers.size() != 1 ? "consumers" : "consumer"});
        } else {
            LOG.log(Level.FINE, "{0}({1} sent to {2} {3}.", new Object[] {event, data, consumers.size(), consumers.size() != 1 ? "consumers" : "consumer"});
        }

        return true;
    }

    public boolean send(String event, T data1, U data2) {
        Set<BiConsumer<T, U>> biConsumers = this.biConsumers.get(event);

        if (biConsumers == null || biConsumers.isEmpty()) {
            return false;
        }

        for (BiConsumer<T, U> consumer : biConsumers) {
            consumer.accept(data1, data2);
        }

        if (data1 == null || data2 == null) {
            LOG.log(Level.FINE, "{0} sent to {1} {2}.", new Object[] {event, consumers.size(), consumers.size() != 1 ? "consumers" : "consumer"});
        } else {
            LOG.log(Level.FINE, "{0}([{1}, {2}] sent to {3} {4}.", new Object[] {event, data1, data2, consumers.size(), consumers.size() != 1 ? "consumers" : "consumer"});
        }

        return true;
    }

    /**
     * Listen for events.
     * @param event event name
     * @param consumer consumer
     * @return <tt>true</tt> if this set did not already contain the specified element
     */
    public boolean listen(String event, Consumer<T> consumer) {
        if (!consumers.containsKey(event)) {
            consumers.put(event, new LinkedHashSet<>());
        }
        
        LOG.log(Level.FINE, "{0} listening on: {1}", new Object[]{consumer, event});

        return consumers.get(event).add(consumer);
    }

    public boolean listen(String event, BiConsumer<T, U> consumer) {
        if (!biConsumers.containsKey(event)) {
            biConsumers.put(event, new LinkedHashSet<>());
        }
        
        LOG.log(Level.FINE, "{0} listening on: {1}", new Object[]{consumer, event});

        return biConsumers.get(event).add(consumer);
    }

    /**
     * Ignore events.
     * @param event event name
     * @param consumer consumer
     * @return
     */
    public boolean ignore(String event, Consumer<T> consumer) {
        Set<Consumer<T>> consumers = this.consumers.get(event);

        if (consumers == null || consumers.isEmpty()) {
            return false;
        }

        return consumers.remove(consumer);
    }

    public boolean ignore(String event, BiConsumer<T, U> consumer) {
        Set<BiConsumer<T, U>> biConsumers = this.biConsumers.get(event);

        if (biConsumers.isEmpty()) {
            return false;
        }

        biConsumers.remove(consumer);

        return true;
    }
}
