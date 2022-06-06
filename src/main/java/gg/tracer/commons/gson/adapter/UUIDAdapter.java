package gg.tracer.commons.gson.adapter;

import com.google.gson.*;
import gg.tracer.commons.gson.GsonAdapter;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * @author Bradley Steele
 */
public class UUIDAdapter implements GsonAdapter<UUID> {

    @Override
    public JsonElement serialize(UUID src, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public UUID deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return UUID.fromString(json.getAsString());
    }
}
