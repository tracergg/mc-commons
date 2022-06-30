package gg.tracer.commons.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Bradley Steele
 */
public final class OfflinePlayers {

    private static final String ENDPOINT_UUID_TO_NAME = "https://api.mojang.com/user/profile/%s";

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private static final LoadingCache<UUID, String> uuidToNameCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<>() {

                @Override
                public String load(UUID uuid) throws Exception {
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(URI.create(String.format(ENDPOINT_UUID_TO_NAME, uuid)))
                            .GET()
                            .build();
                    HttpResponse<String> res = HTTP_CLIENT.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

                    if (res.statusCode() != 200) {
                        return null;
                    }

                    JsonObject object = JsonParser.parseString(res.body()).getAsJsonObject();
                    return object.get("name").getAsString();
                }
            });

    private OfflinePlayers() {}

    public static String getName(UUID uuid, String fallback) {
        String name = uuidToNameCache.getIfPresent(uuid);

        if (name != null) {
            return name;
        }

        name = Bukkit.getOfflinePlayer(uuid).getName();

        if (name != null) {
            return name;
        }

        try {
            return uuidToNameCache.get(uuid);
        } catch (ExecutionException e) {
            // ignored
        }

        return fallback;
    }

    public static String getName(UUID uuid) {
        return getName(uuid, null);
    }
}
