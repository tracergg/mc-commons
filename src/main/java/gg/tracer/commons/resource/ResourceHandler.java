package gg.tracer.commons.resource;

import java.util.Set;

/**
 * @author Bradley Steele
 */
public interface ResourceHandler<T extends Resource> {

    T load(ResourceProvider provider, ResourceReference reference);

    void save(T resource);

    Set<String> getExtensions();
}
