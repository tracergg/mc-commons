package gg.tracer.commons.gson;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * @author Bradley Steele
 */
public interface GsonAdapter<T> extends JsonSerializer<T>, JsonDeserializer<T> {

}
