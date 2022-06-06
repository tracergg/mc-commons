package gg.tracer.commons.resource.yml;

import gg.tracer.commons.resource.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Bradley Steele
 */
public class ResourceYaml extends AbstractResource<ResourceYaml> {

    private ConfigurationSection root;

    public ResourceYaml(ResourceReference reference, ResourceHandler<ResourceYaml> handler) {
        super(reference, handler);
    }

    public ResourceYaml(ResourceYaml resource, ConfigurationSection root) {
        super(resource.reference, resource.handler);
        this.root = root;
    }

    // AbstractResource

    @Override
    public Object getConfiguration() {
        return root;
    }

    @Override
    public void setConfiguration(Object configuration) {
        if (configuration instanceof ConfigurationSection) {
            root = (ConfigurationSection) configuration;
        }
    }

    // ResourceSection

    @Override
    public boolean isRoot() {
        return root != null && root.getParent() == null;
    }

    @Override
    public boolean isSection(String key) {
        return root.isConfigurationSection(key);
    }

    @Override
    public boolean contains(String key, boolean allowNullValue) {
        boolean has = root.contains(key);

        if (allowNullValue) {
            return has;
        }

        return has && root.get(key) != null;
    }

    @Override
    public ResourceSection createSection(String name) {
        if (contains(name)) {
            return getSection(name);
        }

        return new ResourceYaml(this, root.createSection(name));
    }

    @Override
    public String getName() {
        return root.getName();
    }

    @Override
    public String getCurrentPath() {
        return root.getCurrentPath();
    }

    @Override
    public ResourceSection getRoot() {
        Configuration r = root.getRoot();

        if (r == null) {
            return null;
        }

        return new ResourceYaml(this, r);
    }

    @Override
    public ResourceSection getParent() {
        return new ResourceYaml(this, root.getParent());
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return root.getKeys(deep);
    }

    @Override
    public <T> T get(String key, Class<T> type, T def) {
        Object ret = root.get(key);

        if (ret == null || !ret.getClass().isInstance(type)) {
            return def;
        }

        return type.cast(ret);
    }

    @Override
    public String getString(String key, String def) {
        return root.getString(key, def);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return root.getBoolean(key, def);
    }

    @Override
    public byte getByte(String key, byte def) {
        return (byte) root.getInt(key, def);
    }

    @Override
    public char getChar(String key, char def) {
        return (char) root.getInt(key, def);
    }

    @Override
    public short getShort(String key, short def) {
        return (short) root.getInt(key, def);
    }

    @Override
    public int getInt(String key, int def) {
        return root.getInt(key, def);
    }

    @Override
    public long getLong(String key, long def) {
        return root.getLong(key, def);
    }

    @Override
    public float getFloat(String key, float def) {
        return (float) root.getDouble(key, def);
    }

    @Override
    public double getDouble(String key, double def) {
        return root.getDouble(key, def);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        return (List<T>) root.getList(key, new ArrayList<T>());
    }

    @Override
    public ResourceSection getSection(String key) {
        if (!contains(key, false)) {
            return null;
        }

        return new ResourceYaml(this, root.getConfigurationSection(key));
    }

    @Override
    public void set(String key, Object value) {
        root.set(key, value);
    }
}
