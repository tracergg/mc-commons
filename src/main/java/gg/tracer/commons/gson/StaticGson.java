package gg.tracer.commons.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.tracer.commons.gson.adapter.UUIDAdapter;

import java.util.UUID;

/**
 * @author Bradley Steele
 */
public final class StaticGson {

    private static final GsonBuilder GSON_BUILDER = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

    private static Gson GSON = GSON_BUILDER.create();

    private static Gson GSON_PRETTY = GSON_BUILDER
            .setPrettyPrinting()
            .create();


    public static Gson getGson() {
        return GSON;
    }

    public static Gson getGsonPretty() {
        return GSON_PRETTY;
    }

    public static <T> void registerTypeAdapter(Class<T> clazz, GsonAdapter<T> adapter) {
        GSON_BUILDER.registerTypeAdapter(clazz, adapter);

        // rebuild
        GSON = GSON_BUILDER.create();
        GSON_PRETTY = GSON_BUILDER.setPrettyPrinting().create();
    }
}
