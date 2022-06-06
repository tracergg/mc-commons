package gg.tracer.commons.resource.yml;

import com.google.common.collect.Sets;
import gg.tracer.commons.logging.StaticLog;
import gg.tracer.commons.resource.ResourceHandler;
import gg.tracer.commons.resource.ResourceProvider;
import gg.tracer.commons.resource.ResourceReference;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * @author Bradley Steele
 */
public class YamlResourceHandler implements ResourceHandler<ResourceYaml> {

    private final Set<String> extensions = Collections.unmodifiableSet(Sets.newHashSet("yml", "yaml"));

    @Override
    public ResourceYaml load(ResourceProvider provider, ResourceReference reference) {
        ResourceYaml resource = new ResourceYaml(reference, this);
        resource.setConfiguration(YamlConfiguration.loadConfiguration(reference.file));

        return resource;
    }

    @Override
    public void save(ResourceYaml resource) {
        try {
            YamlConfiguration configuration = (YamlConfiguration) resource.getConfiguration();
            configuration.save(resource.reference.file);
        } catch (IOException e) {
            StaticLog.error("An IOException was caught while saving &c%s&r:", resource.reference);
            StaticLog.exception(e);
        }
    }

    @Override
    public Set<String> getExtensions() {
        return extensions;
    }
}
