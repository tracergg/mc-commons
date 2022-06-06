package gg.tracer.commons.resource.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import gg.tracer.commons.gson.StaticGson;
import gg.tracer.commons.resource.AbstractResource;
import gg.tracer.commons.resource.ResourceHandler;
import gg.tracer.commons.resource.ResourceReference;
import gg.tracer.commons.resource.ResourceSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Bradley Steele
 */
public class ResourceJson extends AbstractResource<ResourceJson> {

    private JsonObject root;

    public ResourceJson(ResourceReference reference, ResourceHandler<ResourceJson> handler) {
        super(reference, handler);
    }

    public ResourceJson(ResourceJson resource, JsonObject root) {
        super(resource.reference, resource.handler);
        this.root = root;
    }

    @Override
    public Object getConfiguration() {
        return root;
    }

    @Override
    public void setConfiguration(Object configuration) {
        if (configuration instanceof JsonObject) {
            root = (JsonObject) configuration;
        }
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public boolean isSection(String key) {
        return contains(key, false) && root.get(key).isJsonObject();
    }

    @Override
    public boolean contains(String key, boolean allowNullValue) {
        boolean has = root.has(key);;

        if (allowNullValue) {
            return has;
        }

        return has && root.get(key) != JsonNull.INSTANCE;
    }

    @Override
    public ResourceSection createSection(String name) {
        JsonObject section = new JsonObject();
        root.add(name, section);

        return new ResourceJson(this, section);
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCurrentPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceSection getRoot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceSection getParent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return root.entrySet().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public <T> T get(String key, Class<T> type, T def) {
        JsonElement element = root.get(key);

        if (element == null || element == JsonNull.INSTANCE) {
            return def;
        }

        return StaticGson.getGson().fromJson(element, type);
    }

    @Override
    public String getString(String key, String def) {
        return contains(key, false) ? root.get(key).getAsString() : def;
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return contains(key, false) ? root.get(key).getAsBoolean() : def;
    }

    @Override
    public byte getByte(String key, byte def) {
        return contains(key, false) ? root.get(key).getAsByte() : def;
    }

    @Override
    public char getChar(String key, char def) {
        if (!contains(key, false)) {
            return def;
        }

        String value = root.get(key).getAsString();
        return value.length() >= 1 ? value.charAt(0) : Character.MIN_VALUE;
    }

    @Override
    public short getShort(String key, short def) {
        return contains(key, false) ? root.get(key).getAsShort() : def;
    }

    @Override
    public int getInt(String key, int def) {
        return contains(key, false) ? root.get(key).getAsInt() : def;
    }

    @Override
    public long getLong(String key, long def) {
        return contains(key, false) ? root.get(key).getAsLong() : def;
    }

    @Override
    public float getFloat(String key, float def) {
        return contains(key, false) ? root.get(key).getAsFloat() : def;
    }

    @Override
    public double getDouble(String key, double def) {
        return contains(key, false) ? root.get(key).getAsDouble() : def;
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        return StaticGson.getGson().fromJson(root.getAsJsonArray(key), new TypeToken<ArrayList<T>>(){}.getType());
    }

    @Override
    public ResourceSection getSection(String key) {
        if (!contains(key, false)) {
            return null;
        }

        return new ResourceJson(this, root.getAsJsonObject(key));
    }

    @Override
    public void set(String key, Object value) {
        if (value instanceof ResourceJson) {
            set(key, ((ResourceJson) value).root);
        } else if (value instanceof JsonElement) {
            root.add(key, (JsonElement) value);
        } else if (value == null) {
            root.add(key, JsonNull.INSTANCE);
        } else {
            root.add(key, StaticGson.getGson().toJsonTree(value));
        }
    }
}
