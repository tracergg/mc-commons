package gg.tracer.commons.resource;

import java.util.List;
import java.util.Set;

/**
 * @author Bradley Steele
 */
public interface ResourceSection {

    boolean isRoot();

    boolean isSection(String key);

    boolean contains(String key, boolean allowNullValue);

    default boolean contains(String key) {
        return contains(key, true);
    }

    ResourceSection createSection(String name);

    default ResourceSection getOrCreateSection(String section) {
        return contains(section, false) ? getSection(section) : createSection(section);
    }

    String getName();

    String getCurrentPath();

    ResourceSection getRoot();

    ResourceSection getParent();

    Set<String> getKeys(boolean deep);

    default Set<String> getKeys() {
        return getKeys(false);
    }


    // getters

    <T> T get(String key, Class<T> type, T def);

    default <T> T get(String key, Class<T> type) {
        return get(key, type, null);
    }

    String getString(String key, String def);

    default String getString(String key) {
        return getString(key, null);
    }

    boolean getBoolean(String key, boolean def);

    default boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    byte getByte(String key, byte def);

    default byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    char getChar(String key, char def);

    default char getChar(String key) {
        return getChar(key, Character.MIN_VALUE);
    }

    short getShort(String key, short def);

    default short getShort(String key) {
        return getShort(key, (short) 0);
    }

    int getInt(String key, int def);

    default int getInt(String key) {
        return getInt(key, 0);
    }

    long getLong(String key, long def);

    default long getLong(String key) {
        return getLong(key, 0L);
    }

    float getFloat(String key, float def);

    default float getFloat(String key) {
        return getFloat(key, 0.0F);
    }

    double getDouble(String key, double def);

    default double getDouble(String key) {
        return getDouble(key, 0.0D);
    }

    <T> List<T> getList(String key, Class<T> clazz);

    ResourceSection getSection(String key);

    // setters

    void set(String key, Object value);
}
