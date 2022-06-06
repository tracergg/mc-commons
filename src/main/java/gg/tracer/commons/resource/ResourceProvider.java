package gg.tracer.commons.resource;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import gg.tracer.commons.logging.StaticLog;
import gg.tracer.commons.plugin.TracerPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bradley Steele
 */
public class ResourceProvider {

    private final TracerPlugin plugin;

    private final Map<String, Resource> cached = new HashMap<>();
    private final Map<String, ResourceHandler<?>> handlers = new HashMap<>();

    public ResourceProvider(TracerPlugin plugin) {
        this.plugin = plugin;
    }

    public Resource loadResource(ResourceReference from, ResourceReference to) {
        long start = System.currentTimeMillis();

        try {
            Files.createParentDirs(to.file);

            if (!to.file.exists()) {
                try (InputStream is = plugin.getResource(from.path.replace("\\", "/")); OutputStream os = new FileOutputStream(to.file)) {
                    ByteStreams.copy(is, os);
                    plugin.logger.info("Loaded resource defaults for &a%s&r (time: &2%s&rms)", to.path, (System.currentTimeMillis() - start));
                }
            }
        } catch (IOException e) {
            plugin.logger.error("An IOException was caught while generating resource defaults for [&c%s&r]:", to.path);
            plugin.logger.exception(e);
        }

        ResourceHandler<?> handler = getResourceHandler(to.ext);

        if (handler == null) {
            return null;
        }

        Resource resource = handler.load(this, to);
        cached.put(to.path, resource);

        return resource;
    }

    public Resource loadResource(File from, File to) {
        return loadResource(new ResourceReference(from), new ResourceReference(to));
    }

    public Resource loadResource(String from, String to) {
        return loadResource(new File(from), new File(to));
    }

    public Resource loadResource(ResourceReference reference) {
        return loadResource(reference, new ResourceReference(new File(plugin.getDataFolder(), reference.path)));
    }

    public Resource loadResource(File file) {
        return loadResource(new ResourceReference(file));
    }

    public Resource loadResource(String path) {
        return loadResource(new File(path));
    }

    public void saveResource(Resource resource) {
        resource.save();
    }

    public void addResourceHandler(ResourceHandler<?> handler) {
        for (String ext : handler.getExtensions()) {
            handlers.put(ext, handler);
        }
    }

    public Resource getResource(ResourceReference reference, boolean fromCache) {
        if (fromCache && cached.containsKey(reference.path)) {
            return cached.get(reference.path);
        }

        return loadResource(reference);
    }

    public Resource getResource(ResourceReference reference) {
        return getResource(reference, true);
    }

    private ResourceHandler<?> getResourceHandler(String extension) {
        extension = extension.toLowerCase();

        for (ResourceHandler<?> handler : handlers.values()) {
            if (handler.getExtensions().contains(extension)) {
                return handler;
            }
        }

        return null;
    }
}
