package gg.tracer.commons.register.store;

import com.google.common.collect.Lists;
import gg.tracer.commons.plugin.TracerPlugin;
import gg.tracer.commons.register.Registrable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Bradley Steele
 */
public class Store<T> implements Registrable, Listener {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final Map<Object, T> store = new HashMap<>();
    protected TracerPlugin plugin;

    @Override
    public final void internalRegister() {
        plugin.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public final void internalUnregister() {
        HandlerList.unregisterAll(this);
        store.clear();
    }

    public Map<Object, T> all() {
        return store;
    }

    public Set<Object> keys() {
        return store.keySet();
    }

    public Collection<T> values() {
        return store.values();
    }

    public boolean exists(Object key) {
        return key != null && store.containsKey(key);
    }

    public boolean exists(String key) {
        return key != null && exists((Object) key.toLowerCase());
    }

    public T retrieve(Object key) {
        return key != null ? store.get(key) : null;
    }

    public T retrieve(String key) {
        return key != null ? retrieve((Object) key.toLowerCase()) : null;
    }

    public T retrieveRandom(Collection<T> exclude) {
        List<T> values = new ArrayList<>(store.values());
        values.removeAll(exclude);

        if (values.isEmpty()) {
            return null;
        }

        return values.get(RANDOM.nextInt(values.size()));
    }

    @SafeVarargs
    public final T retrieveRandom(T... exclude) {
        return retrieveRandom(Arrays.asList(exclude));
    }

    public T retrieveRandom(Predicate<T> predicate) {
        List<T> values = store.values()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());

        if (values.isEmpty()) {
            return null;
        }

        return values.get(RANDOM.nextInt(values.size()));
    }

    public T store(Object key, T value) {
        if (key == null) {
            return null;
        }

        return store.put(key, value);
    }

    public T store(String key, T value) {
        if (key == null) {
            return null;
        }

        return store((Object) key.toLowerCase(), value);
    }

    public void storeAll(Map<Object, ? extends T> map) {
        store.putAll(map);
    }

    public T drop(Object key) {
        if (key == null) {
            return null;
        }

        return store.remove(key);
    }

    public T drop(String key) {
        if (key == null) {
            return null;
        }

        return drop((Object) key.toLowerCase());
    }
}
