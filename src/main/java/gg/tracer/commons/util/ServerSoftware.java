package gg.tracer.commons.util;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Bradley Steele
 */
public final class ServerSoftware {

    private ServerSoftware() {}

    private static AtomicBoolean _IS_SPIGOT = null;

    public static boolean isSpigot() {
        if (_IS_SPIGOT == null) {
            _IS_SPIGOT = new AtomicBoolean(Reflection.getClass("org.bukkit.Server$Spigot") != null);
        }

        return _IS_SPIGOT.get();
    }
}
