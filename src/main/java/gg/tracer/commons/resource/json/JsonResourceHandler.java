package gg.tracer.commons.resource.json;

import com.google.common.collect.Sets;
import com.google.gson.JsonParser;
import gg.tracer.commons.gson.StaticGson;
import gg.tracer.commons.logging.StaticLog;
import gg.tracer.commons.resource.ResourceHandler;
import gg.tracer.commons.resource.ResourceProvider;
import gg.tracer.commons.resource.ResourceReference;

import java.io.*;
import java.util.Collections;
import java.util.Set;

/**
 * @author Bradley Steele
 */
public class JsonResourceHandler implements ResourceHandler<ResourceJson> {

    private final Set<String> extensions = Collections.unmodifiableSet(Sets.newHashSet("json"));

    @Override
    public ResourceJson load(ResourceProvider provider, ResourceReference reference) {
        ResourceJson resource = new ResourceJson(reference, this);

        try (var reader = new BufferedReader(new InputStreamReader(new FileInputStream(reference.file)))) {
            resource.setConfiguration(JsonParser.parseReader(reader).getAsJsonObject());
        } catch (Exception e) {
            StaticLog.error("An Exception was caught while loading &c%s&r:", resource.reference);
            StaticLog.exception(e);
        }

        return resource;
    }

    @Override
    public void save(ResourceJson resource) {
        try (var writer = new OutputStreamWriter(new FileOutputStream(resource.reference.file))) {
            writer.write(StaticGson.getGsonPretty().toJson(resource.getConfiguration()));
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
